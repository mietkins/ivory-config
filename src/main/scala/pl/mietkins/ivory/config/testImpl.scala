package pl.mietkins.ivory.config

import com.typesafe.config.Config

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object testImpl {

  import impl._

  def getFromConfigImplTest[T: c.WeakTypeTag](c: Context): c.Expr[Config => T] = {

    import c.universe._

    val tree = handleCaseClass(c)(c.weakTypeOf[T])

    println("=" * 10, "RAW", "=" * 10)

    println(showRaw(tree))

    println("=" * 10, "CODE", "=" * 10)

    println(show(tree))

    c.Expr[Config => T](tree)
  }
}
