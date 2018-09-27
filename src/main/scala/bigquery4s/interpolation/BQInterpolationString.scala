package bigquery4s.interpolation

import bigquery4s.{BigQuery, ProjectId, WrappedTableRow}

class BQInterpolationString(private val s: StringContext) extends AnyVal {

  /**
    * StringContext for the standard SQL
    *
    * @param args he arguments to be inserted into the resulting query.
    * @param bq implicit BigQuery configuration
    * @param projectId implicit declaration of ProjectId
    * @return
    */
  def bqs(args: Any*)(bq: BigQuery, projectId: ProjectId): Seq[WrappedTableRow] =
    bq.getRows(
      bq.await(
        bq.startStandardQuery(
          projectId,
          s.standardInterpolator(StringContext.treatEscapes, args))))

  /**
    * StringContext for the legacy SQL
    *
    * @param args he arguments to be inserted into the resulting query.
    * @param bq implicit BigQuery configuration
    * @param projectId implicit declaration of ProjectId
    * @return
    */
  def bql(args: Any*)(bq: BigQuery, projectId: ProjectId): Seq[WrappedTableRow] =
    bq.getRows(
      bq.await(
        bq.startQuery(
          projectId,
          s.standardInterpolator(StringContext.treatEscapes, args))))

}
