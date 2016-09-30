package pl.mietkins.ivory.config

import com.typesafe.config.ConfigValue

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import pl.mietkins.ivory.config.Impl._

object TestImpl {

  def getFromConfigImplTest[T: c.WeakTypeTag](c: Context): c.Expr[ConfigValue => T] = {

    import c.universe._

    val tree =
      q"""
          import scala.collection.JavaConversions._
          import com.typesafe.config.{ConfigValue, ConfigObject, ConfigList}
          ${handleConfig(c)(c.weakTypeOf[T])}"""

    // scalastyle:off

    println("=" * 10, "RAW", "=" * 10)

    println(showRaw(tree))

    println("=" * 10, "CODE", "=" * 10)

    println(show(tree))

    // scalastyle:on

    c.Expr[ConfigValue => T](tree)
  }
}
