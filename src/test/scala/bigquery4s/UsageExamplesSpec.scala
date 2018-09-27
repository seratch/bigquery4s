package bigquery4s

import org.scalatest._
import org.slf4j.LoggerFactory

/**
 * requirement: $HOME/.bigquery/client_secret.json
 */
class UsageExamplesSpec extends FunSpec with Matchers {

  lazy val logger = LoggerFactory.getLogger(classOf[UsageExamplesSpec])

  val yourOwnProjectId: String = "thinking-digit-98014"
  implicit val bq: BigQuery = BigQuery()
  implicit val projectId: ProjectId = ProjectId(yourOwnProjectId)

  describe("Simple query example with publicdata") {
    it("runs") {

      val datasets: Seq[WrappedDatasets] = bq.listDatasets("publicdata")
      logger.info(datasets.mkString("\n"))

      val query = """
        SELECT weight_pounds,state,year,gestation_weeks FROM publicdata:samples.natality
        ORDER BY weight_pounds DESC LIMIT 100;
      """
      val jobId: JobId = bq.startQuery(yourOwnProjectId, query)
      logger.info(s"JobId: $jobId")

      val job: WrappedCompletedJob = bq.await(jobId)
      logger.info(s"Job: $job")

      val rows: Seq[WrappedTableRow] = bq.getRows(job)
      val sampleCsv = rows.take(20).map(_.cells.map(_.value.orNull).mkString(","))
      logger.info(s"""
       |*** QueryResult ***
       |
       |weight_pounds,state,year,gestation_weeks
       |${sampleCsv.mkString("\n")}
       |""".stripMargin)
    }
  }

  describe("Simple legacy sql query example with publicdata") {
    it("runs") {
      import bigquery4s.interpolation.Implicits._

      val rows: Seq[WrappedTableRow] =
        bql"SELECT weight_pounds,state,year,gestation_weeks FROM publicdata:samples.natality ORDER BY weight_pounds DESC LIMIT 100;"
      val sampleCsv = rows.take(20).map(_.cells.map(_.value.orNull).mkString(","))

      logger.info(s"""
        |*** QueryResult ***
        |
        |weight_pounds,state,year,gestation_weeks
        |${sampleCsv.mkString("\n")}
        |""".stripMargin)
    }
  }

  describe("Simple standard sql query example with publicdata") {
    it("runs") {
      import bigquery4s.interpolation.Implicits._

      val rows: Seq[WrappedTableRow] =
        bql"SELECT weight_pounds,state,year,gestation_weeks FROM publicdata.samples.natality ORDER BY weight_pounds DESC LIMIT 100;"
      val sampleCsv = rows.take(20).map(_.cells.map(_.value.orNull).mkString(","))

      logger.info(s"""
        |*** QueryResult ***
        |
        |weight_pounds,state,year,gestation_weeks
        |${sampleCsv.mkString("\n")}
        |""".stripMargin)
    }
  }
}
