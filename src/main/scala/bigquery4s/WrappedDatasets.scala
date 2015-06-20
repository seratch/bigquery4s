package bigquery4s

import com.google.api.client.json.JsonFactory
import com.google.api.client.util.ClassInfo
import com.google.api.services.bigquery.model.DatasetList.Datasets
import scala.collection.JavaConverters._

/**
 * Datasets Scala wrapper
 */
case class WrappedDatasets(underlying: Datasets) {

  lazy val datasetReference: WrappedDatasetReference = WrappedDatasetReference(underlying.getDatasetReference)
  lazy val friendlyName: String = underlying.getFriendlyName
  lazy val id: String = underlying.getId
  lazy val kind: String = underlying.getKind
  lazy val classInfo: ClassInfo = underlying.getClassInfo
  lazy val factory: JsonFactory = underlying.getFactory
  lazy val unknownKeys: Map[String, AnyRef] = underlying.getUnknownKeys.asScala.toMap

}
