package bigquery4s.interpolation

import bigquery4s.{BigQuery, ProjectId}

trait BQSyntax extends Implicits {

  implicit val bq: BigQuery
  implicit val projectId: ProjectId

}
