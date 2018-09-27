package bigquery4s

import java.io.{ File, FileInputStream, InputStreamReader }

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{ GoogleAuthorizationCodeFlow, GoogleClientSecrets, GoogleCredential }
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet._
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.{ DataStoreFactory, FileDataStoreFactory }
import com.google.api.services.bigquery.model._
import com.google.api.services.bigquery.{ Bigquery, BigqueryScopes }

import scala.annotation.tailrec
import scala.collection.JavaConverters._

/**
 * BigQuery SDK Wrapper
 *
 * https://cloud.google.com/bigquery/bigquery-api-quickstart
 */
case class BigQuery(
  transport: HttpTransport,
  jsonFactory: JsonFactory,
  credential: Credential,
  applicationName: String) {

  lazy val underlying: Bigquery = {
    new Bigquery.Builder(transport, jsonFactory, credential)
      .setApplicationName(applicationName)
      .build()
  }

  def listDatasets(projectId: String): Seq[WrappedDatasets] = listDatasets(ProjectId(projectId))

  def listDatasets(projectId: ProjectId): Seq[WrappedDatasets] = {
    Option(underlying.datasets.list(projectId.value).execute().getDatasets)
      .map(_.asScala.toSeq.map(d => WrappedDatasets(d)))
      .getOrElse(Seq.empty)
  }

  // TODO: tables ops

  def startStandardQuery(projectId: String, query: String): JobId = startQuery(ProjectId(projectId), query, useLegacy = false)

  def startStandardQuery(projectId: ProjectId, query: String): JobId = startQuery(projectId, query, useLegacy = false)

  def startQuery(projectId: String, query: String): JobId = startQuery(ProjectId(projectId), query)

  def startQuery(projectId: ProjectId, query: String, useLegacy: Boolean = true): JobId = {
    val job: Job = {
      val config = new JobConfiguration
      val queryConfig = new JobConfigurationQuery().setUseLegacySql(useLegacy)
      config.setQuery(queryConfig)
      val j = new Job
      j.setConfiguration(config)
      j
    }
    job.getConfiguration.getQuery.setQuery(query)

    val insertion: Bigquery#Jobs#Insert = {
      val i = underlying.jobs.insert(projectId.value, job)
      i.setProjectId(projectId.value)
      i
    }

    JobId(projectId, insertion.execute().getJobReference.getJobId)
  }

  def await(jobId: JobId, timeoutSeconds: Int = 300): WrappedCompletedJob = {
    var job = underlying.jobs.get(jobId.projectId.value, jobId.value).execute()
    (1 to timeoutSeconds).foreach { i =>
      if (job.getStatus.getState != "DONE") {
        Thread.sleep(1000)
        job = underlying.jobs.get(jobId.projectId.value, jobId.value).execute()
      }
    }
    if (job.getStatus.getState != "DONE") {
      throw new JobTimeoutException(jobId)
    } else {
      WrappedCompletedJob(job)
    }
  }

  def getRows(job: WrappedCompletedJob): Seq[WrappedTableRow] = getRows(job.underlying)

  def getRows(job: Job): Seq[WrappedTableRow] = {
    val response: GetQueryResultsResponse = underlying.jobs
      .getQueryResults(job.getJobReference.getProjectId, job.getJobReference.getJobId)
      .execute()
    readPage(response, wrapRows(response), response.getPageToken)
  }

  private def wrapRows(response: GetQueryResultsResponse) =
    Option(response.getRows).map(_.asScala.toSeq.map(WrappedTableRow)).getOrElse(Nil)

  @tailrec
  private def readPage(
    qr: GetQueryResultsResponse,
    acc: Seq[WrappedTableRow],
    pageToken: String): Seq[WrappedTableRow] =
    if (pageToken != null) {
      val jr = qr.getJobReference
      val response = underlying.jobs().getQueryResults(jr.getProjectId, jr.getJobId).setPageToken(pageToken).execute()
      val pageRows = wrapRows(response)
      readPage(response, acc ++ pageRows, response.getPageToken)
    } else
      acc

}

object BigQuery {

  def apply(
    transport: HttpTransport = new NetHttpTransport,
    jsonFactory: JsonFactory = new JacksonFactory,
    clientSecretJsonPath: String = homeDir + "/.bigquery/client_secret.json",
    scopes: Seq[String] = Seq(BigqueryScopes.BIGQUERY),
    dataStoreFactory: DataStoreFactory = new FileDataStoreFactory(new File(homeDir, ".bigquery/datastore/default")),
    applicationName: String = "bigquery4s-client"): BigQuery = {

    fromClientSecretJson(transport, jsonFactory, clientSecretJsonPath, scopes, dataStoreFactory)
  }

  def fromClientSecretJson(
    transport: HttpTransport = new NetHttpTransport,
    jsonFactory: JsonFactory = new JacksonFactory,
    clientSecretJsonPath: String = homeDir + "/.bigquery/client_secret.json",
    scopes: Seq[String] = Seq(BigqueryScopes.BIGQUERY),
    dataStoreFactory: DataStoreFactory = new FileDataStoreFactory(new File(homeDir, ".bigquery/datastore/default")),
    applicationName: String = "bigquery4s-client"): BigQuery = {

    val clientSecrets: GoogleClientSecrets =
      using(new FileInputStream(clientSecretJsonPath)) { in =>
        using(new InputStreamReader(in)) { reader =>
          GoogleClientSecrets.load(jsonFactory, reader)
        }
      }

    val flow = new GoogleAuthorizationCodeFlow.Builder(
      transport,
      jsonFactory,
      clientSecrets,
      scopes.asJava).setDataStoreFactory(dataStoreFactory).build()

    val credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")

    BigQuery(transport, jsonFactory, credential, applicationName)
  }

  def fromServiceAccount(
    serviceAccountId: String,
    serviceAccountPrivateKeyP12FilePath: String = homeDir + "/.bigquery/service_account.p12",
    transport: HttpTransport = new NetHttpTransport,
    jsonFactory: JsonFactory = new JacksonFactory,
    scopes: Seq[String] = Seq(BigqueryScopes.BIGQUERY),
    applicationName: String = "bigquery4s-client"): BigQuery = {

    val credential = new GoogleCredential.Builder()
      .setTransport(transport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountId(serviceAccountId)
      .setServiceAccountPrivateKeyFromP12File(new File(serviceAccountPrivateKeyP12FilePath))
      .setServiceAccountScopes(scopes.asJava)
      .build()

    BigQuery(transport, jsonFactory, credential, applicationName)
  }

  def fromServiceAccountJson(
    serviceAccountJsonFilePath: String = homeDir + "/.bigquery/service_account.json",
    transport: HttpTransport = new NetHttpTransport,
    jsonFactory: JsonFactory = new JacksonFactory,
    scopes: Seq[String] = Seq(BigqueryScopes.BIGQUERY),
    applicationName: String = "bigquery4s-client"): BigQuery = {

    val credential = using(new FileInputStream(serviceAccountJsonFilePath)) { in =>
      GoogleCredential.fromStream(in).createScoped(scopes.asJava)
    }

    BigQuery(transport, jsonFactory, credential, applicationName)
  }

  def fromDefaultInstance(
    transport: HttpTransport = new NetHttpTransport,
    jsonFactory: JsonFactory = new JacksonFactory,
    scopes: Seq[String] = Seq(BigqueryScopes.BIGQUERY),
    applicationName: String = "bigquery4s-client"): BigQuery = {

    val credential = GoogleCredential.getApplicationDefault

    BigQuery(transport, jsonFactory, credential, applicationName)
  }
}