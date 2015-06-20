package bigquery4s

import com.google.api.services.bigquery.model.{ TableCell, TableRow }
import scala.collection.JavaConverters._

/**
 * TableRow wrapper
 */
case class WrappedTableRow(underlying: TableRow) {

  lazy val cells: Seq[WrappedTableCell] = {
    underlying.getF.asScala.map(cell => WrappedTableCell(cell)).toSeq
  }

}
