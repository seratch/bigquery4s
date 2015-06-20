import scala.language.reflectiveCalls

package object bigquery4s {

  type Closable = { def close() }

  /**
   * Closes the resource finally.
   */
  def using[R <: Closable, A](resource: R)(f: R => A): A = {
    try {
      f(resource)
    } finally {
      try {
        resource.close()
      } catch {
        case ignore: Exception =>
      }
    }
  }

  lazy val homeDir: String = System.getProperty("user.home")

}
