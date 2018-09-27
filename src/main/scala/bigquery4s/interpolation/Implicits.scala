package bigquery4s.interpolation

import scala.language.implicitConversions

/**
  * object to import.
  */
object Implicits extends Implicits

/**
  * Implicit conversion imports.
  */
trait Implicits {

  /**
    * Enables bqs"", bql"" interpolation.
    *
    * {{{
    *   bqs"select * from ds,members"
    *   bql"select * from ds.members"
    * }}}
    */
  @inline implicit def bigquery4sSQLInterpolationImplicitDef(s: StringContext): BQInterpolationString = new BQInterpolationString(s)
}

