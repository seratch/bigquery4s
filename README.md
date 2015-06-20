### bigquery4s

A handy Scala wrapper of Google BigQuery API 's Java Client Library.

#### Getting Started

Read BigQuery API Quickstart for details.

https://cloud.google.com/bigquery/bigquery-api-quickstart

##### Create a New BigQuery Project

- Visit the [Google Developers Console](https://console.developers.google.com/project), create a new project.
- Expand APIS & AUTH, and click on APIs. In that list, search for and enable the BigQuery API.
- Finally, note the number of the project you just created. You can find the project number listed at the top of the project overview page for your project: https://console.developers.google.com/project/apps~

##### Generate a Client ID and Client Secret for your Application

- Visit the [Google Developers Console](https://console.developers.google.com/project) and select the project you just created.
- Click on APIS & AUTH on the left, then select Credentials. Click on "Create new Client ID."
- Select Installed application.
- Click Create client ID, then click Download JSON to download the client file.
- Copy the downloaded file to a location accessible from your application, and give it a name like client_secrets.json. Your application will refer to this file as part of the flow of authorizing access to the BigQuery API. For more information about using the client_secrets.json format, see [this page](https://developers.google.com/api-client-library/python/guide/aaa_client_secrets).

Save the JSON file as `$HOME/.bigquery/client_secret.json`(the default path).

##### Setting up sbt project

If you don't have sbt installed yet, read the official guide:

http://www.scala-sbt.org/0.13/tutorial/Setup.html

If you're a MacOS X user, just `brew install sbt` will work fine for you.

```sh
brew install sbt # MacOS X

mkdir -p $HOME/bq-sample/project
cd $HOME/bq-sample

echo 'libraryDependencies += "com.github.seratch" %% "bigquery4s" % "0.1"
scalaVersion := "2.11.6"' > build.sbt
echo "sbt.version=0.13.8" > project/build.properties
sbt console
```

##### Run Examples

Try the following examples on the `sbt console`.

```scala
import bigquery4s._

val bq = BigQuery() // expects $HOME/.bigquery/client_secret.json

val ds = bq.listDatasets("publicdata")
// ds: Seq[bigquery4s.WrappedDatasets] = ArrayBuffer(WrappedDatasets({"datasetReference":{"datasetId":"samples","projectId":"publicdata"},"id":"publicdata:samples","kind":"bigquery#dataset"}))

val yourOwnProjectId = "CHANGE THIS"
val jobId = bq.startQuery(yourOwnProjectId,
  "SELECT weight_pounds,state,year,gestation_weeks FROM publicdata:samples.natality ORDER BY weight_pounds DESC LIMIT 100")
// jobId: bigquery4s.JobId = JobId(ProjectId(thinking-digit-98014),job_yzM_VroG0wbj1CIeLh4U4u6V1BI)

val job = bq.await(jobId)
// job: bigquery4s.WrappedCompletedJob = WrappedCompletedJob({"configuration":{"query":{"createDisposition":"CREATE_IF_NEEDED","destinationTable":{"datasetId":"_ce67ab3da040a9d26fa59261366d42efce66d7a2","projectId":"thinking-digit-98014","tableId":"anoncda8bb0f9e2201ba87406ed02b1681750920f9af"},"query":"SELECT weight_pounds,state,year,gestation_weeks FROM publicdata:samples.natality ORDER BY weight_pounds DESC LIMIT 100","writeDisposition":"WRITE_TRUNCATE"}},"etag":"\"Gn3Hpo5WaKnpFuT457VBDNMgZBw/8QF2L_W0fUFwBdI0pxm5gUIc6dw\"","id":"thinking-digit-98014:job_DgtpBcg85jewo3soMIwXDH0v-r8","jobReference":{"jobId":"job_DgtpBcg85jewo3soMIwXDH0v-r8","projectId":"thinking-digit-98014"},"kind":"bigquery#job","selfLink":"https://www.googleapis.com/bigquery/v2/projects/thinking-digit-98014/jobs/job_Dg...

val rows = bq.getRows(job)
// rows: Seq[bigquery4s.WrappedTableRow] = ArrayBuffer(WrappedTableRow({"f":[{"v":"18.0007436923"},{"v":null},{"v":"2005"},{"v":null}]}), WrappedTableRow({"f":[{"v":"18.0007436923"},{"v":null},{"v":"2005"},{"v":"37"}]}), WrappedTableRow({"f":[{"v":"18.0007436923"},{"v":"KY"},{"v":"2004"},{"v":"47"}]}), WrappedTableRow({"f":[{"v":"18.0007436923"},{"v":"WY"},{"v":"1979"},{"v":"35"}]}), WrappedTableRow({"f":[{"v":"18.0007436923"},{"v":null},{"v":"2006"},{"v":"39"}]}), WrappedTableRow({"f":[{"v":"18.0007436923"},{"v":null},{"v":"2006"},{"v":"37"}]}), WrappedTableRow({"f":[{"v":"18.0007436923"},{"v":"KY"},{"v":"2004"},{"v":"38"}]}), WrappedTableRow({"f":[{"v":"18.0007436923"},{"v":"KY"},{"v":"2004"},{"v":"39"}]}), WrappedTableRow({"f":[{"v":"18.0007436923"},{"v":"KY"},{"v":"2004"},{"v":"47"}]})...

println(rows.take(10).map(_.cells.map(_.value.orNull).mkString(",")).mkString("\n"))

18.0007436923,null,2005,null
18.0007436923,null,2005,37
18.0007436923,KY,2004,47
18.0007436923,WY,1979,35
18.0007436923,null,2006,39
18.0007436923,null,2006,37
18.0007436923,KY,2004,38
18.0007436923,KY,2004,39
18.0007436923,KY,2004,47
18.0007436923,UT,1982,33
```

See more examples [here](https://github.com/seratch/bigquery4s/tree/master/src/test/scala/bigquery4s/UsageExamplesSpec.scala).

#### License

(The MIT License)

Copyright (c) 2015 Kazuhiro Sera <seratch_at_gmail.com>

