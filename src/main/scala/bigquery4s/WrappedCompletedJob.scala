package bigquery4s

import com.google.api.client.json.JsonFactory
import com.google.api.client.util.ClassInfo
import com.google.api.services.bigquery.model._
import scala.collection.JavaConverters._

/**
 * A BigQuery job which status is already "DONE"
 */
case class WrappedCompletedJob(underlying: Job) {

  lazy val configuration: JobConfiguration = underlying.getConfiguration
  lazy val etag: String = underlying.getEtag
  lazy val id: String = underlying.getId
  lazy val jobReference: JobReference = underlying.getJobReference
  lazy val kind: String = underlying.getKind
  lazy val selfLink: String = underlying.getSelfLink
  lazy val statistics: JobStatistics = underlying.getStatistics
  lazy val status: JobStatus = underlying.getStatus
  lazy val userEmail: String = underlying.getUserEmail
  lazy val classInfo: ClassInfo = underlying.getClassInfo
  lazy val factory: JsonFactory = underlying.getFactory
  lazy val unknownKeys: Map[String, AnyRef] = underlying.getUnknownKeys.asScala.toMap

}
