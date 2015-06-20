package bigquery4s

case class JobTimeoutException(jobId: JobId)
  extends Exception(s"Job: ${jobId}")
