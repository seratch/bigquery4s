package bigquery4s

import java.io.{ File, InputStreamReader, FileInputStream }
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{ GoogleClientSecrets, GoogleAuthorizationCodeFlow }
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet._
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.{ DataStoreFactory, FileDataStoreFactory }
import com.google.api.services.bigquery.model._
import com.google.api.services.bigquery.{ Bigquery, BigqueryScopes }
import scala.collection.JavaConverters._

/**
 * BigQuery SDK Wrapper
 *
 * https://cloud.google.com/bigquery/bigquery-api-quickstart
 */
case class BigQuery(
    transport: HttpTransport = new NetHttpTransport,
    jsonFactory: JsonFactory = new JacksonFactory,
    clientSecretJsonPath: String = homeDir + "/.bigquery/client_secret.json",
    scopes: Seq[String] = Seq(BigqueryScopes.BIGQUERY),
    dataStoreFactory: DataStoreFactory = new FileDataStoreFactory(new File(homeDir, ".bigquery/datastore/default"))) {

  lazy val clientSecrets: GoogleClientSecrets = {
    using(new FileInputStream(clientSecretJsonPath)) { in =>
      using(new InputStreamReader(in)) { reader =>
        GoogleClientSecrets.load(jsonFactory, reader)
      }
    }
  }

  private[this] def authorize(): Credential = {
    val flow = new GoogleAuthorizationCodeFlow.Builder(
      transport,
      jsonFactory,
      clientSecrets,
      scopes.asJava
    ).setDataStoreFactory(dataStoreFactory).build()

    new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")
  }

  lazy val underlying: Bigquery = {
    new Bigquery(transport, jsonFactory, authorize())
  }

  def listDatasets(projectId: String): Seq[WrappedDatasets] = listDatasets(ProjectId(projectId))

  def listDatasets(projectId: ProjectId): Seq[WrappedDatasets] = {
    Option(underlying.datasets.list(projectId.value).execute().getDatasets)
      .map(_.asScala.toSeq.map(d => WrappedDatasets(d)))
      .getOrElse(Seq.empty)
  }

  // TODO: tables ops

  def startQuery(projectId: String, query: String): JobId = startQuery(ProjectId(projectId), query)

  def startQuery(projectId: ProjectId, query: String): JobId = {
    val job: Job = {
      val config = new JobConfiguration
      val queryConfig = new JobConfigurationQuery
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
    val job = underlying.jobs.get(jobId.projectId.value, jobId.value).execute()
    (1 to timeoutSeconds).foreach { i =>
      if (job.getStatus.getState != "DONE") {
        Thread.sleep(1000)
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
    response.getRows.asScala.toSeq.map(WrappedTableRow)
  }

}
