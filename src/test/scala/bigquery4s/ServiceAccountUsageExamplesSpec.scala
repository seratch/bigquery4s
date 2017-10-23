package bigquery4s

import java.io.FileInputStream

import org.scalatest._
import org.slf4j.LoggerFactory

/**
 * requirement: $HOME/.bigquery/service_account.json
 */
class ServiceAccountUsageExamplesSpec extends FunSpec with Matchers {

  lazy val logger = LoggerFactory.getLogger(classOf[UsageExamplesSpec])
  val yourOwnProjectId = "thinking-digit-98014"

  describe("Simple query example with publicdata using service account json file") {
    it("runs") {
      val bq = BigQuery.fromServiceAccountJson()

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

  describe("Simple query example with publicdata using service account json string") {
    it("runs") {
      using(new FileInputStream(homeDir + "/.bigquery/service_account.json")) { in =>
        val bq = BigQuery.fromServiceAccountJsonInputStream(in)

        val datasets: Seq[WrappedDatasets] = bq.listDatasets("publicdata")
        logger.info(datasets.mkString("\n"))

        val query =
          """
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
                       |${
          sampleCsv.
            mkString("\n")
        }
                       |""".stripMargin)
      }
    }
  }
}
