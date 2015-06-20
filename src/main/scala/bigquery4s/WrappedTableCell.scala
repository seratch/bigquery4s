package bigquery4s

import com.google.api.client.util.Data
import com.google.api.services.bigquery.model.TableCell

/**
 * TableCell wrapper
 */
case class WrappedTableCell(private val underlying: TableCell) {

  lazy val value: Option[AnyRef] = {
    if (Data.isNull(underlying.getV)) None
    else Some(underlying.getV)
  }

}