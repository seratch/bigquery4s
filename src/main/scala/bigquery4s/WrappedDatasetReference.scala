package bigquery4s

import com.google.api.client.json.JsonFactory
import com.google.api.client.util.ClassInfo
import com.google.api.services.bigquery.model.DatasetReference
import scala.collection.JavaConverters._

/**
 * DatasetReference wrapper
 */
case class WrappedDatasetReference(underlying: DatasetReference) {

  lazy val datasetId: DatasetId = DatasetId(underlying.getDatasetId)
  lazy val projectId: ProjectId = ProjectId(underlying.getProjectId)
  lazy val classInfo: ClassInfo = underlying.getClassInfo
  lazy val factory: JsonFactory = underlying.getFactory
  lazy val unknownKeys: Map[String, AnyRef] = underlying.getUnknownKeys.asScala.toMap

}