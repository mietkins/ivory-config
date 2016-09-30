package pl.mietkins.ivory.config

import com.typesafe.config.Config

import scala.reflect.macros.blackbox.Context

object Impl {

  private val supportedTypesString =
    """"
    Supported types:
      T ∈ [case class, List[T], Option[T], String, Boolean, Int, Long, Double]
    """

  private def isCaseClass(c: Context)(symbol: c.Symbol): Boolean = symbol.isClass && symbol.asClass.isCaseClass

  private def getCaseClassAccessors(c: Context)(tpe: c.Type): List[c.Symbol] = {
    tpe.members.collect({
      case m: c.universe.MethodSymbol if m.isCaseAccessor => m
    }).toList
  }

  private def matchAnyVal(c: Context)(path: String): PartialFunction[c.Type, c.Tree] = {
    import c.universe._
    {
      case t if t <:< c.typeOf[AnyVal] || t =:= c.typeOf[String] => {
        val methodName = s"get${t.typeSymbol.name.decodedName.toString}"
        q"c.${TermName(methodName)}(${path})"
      }
    }
  }

  private def matchOption(c: Context)(path: String)(implicit ttag: c.TypeTag[Option[_]]): PartialFunction[c.Type, c.Tree] = {
    import c.universe._
    {
      case t if t <:< c.typeOf[Option[_]] => {
        val innerTree = handlePath(c)(t.typeArgs.head, path)
        q"if(c.hasPath($path)) Some(${innerTree}) else None"
      }
    }
  }

  private def matchList(c: Context)(path: String)(implicit ttag: c.TypeTag[List[_]]): PartialFunction[c.Type, c.Tree] = {
    import c.universe._
    {
      case t if t <:< c.typeOf[List[_]] => {
        val typeParam = t.typeArgs.head

        if (isCaseClass(c)(typeParam.typeSymbol)) {
          q"c.getConfigList($path).toList.map(e => ${handleCaseClass(c)(typeParam)}(e))"
        } else {
          val symbolName = typeParam.typeSymbol.name.decodedName.toString
          val methodName = s"get${symbolName}List"
          q"c.${TermName(methodName)}($path).map(e => e : ${TypeName(symbolName)}).toList"
        }
      }
    }
  }

  private def matchCaseClass(c: Context)(path: String): PartialFunction[c.Type, c.Tree] = {
    import c.universe._
    {
      case t if isCaseClass(c)(t.typeSymbol) => q"${handleCaseClass(c)(t)}(c.getConfig(${path}))"
    }
  }

  private def matchNotImplemented(c: Context)(path: String): PartialFunction[c.Type, c.Tree] = {
    case t => throw new NotImplementedError(supportedTypesString)
  }

  private def handlePath(c: Context)(tpe: c.Type, path: String): c.Tree = {
    (matchAnyVal(c)(path) orElse
      matchOption(c)(path) orElse
      matchList(c)(path) orElse
      matchCaseClass(c)(path) orElse
      matchNotImplemented(c)(path))(tpe.resultType)
  }

  def handleCaseClass(c: Context)(tpe: c.Type): c.Tree = {
    import c.universe._

    assert(isCaseClass(c)(tpe.typeSymbol), "Root type must be case class")

    val params = getCaseClassAccessors(c)(tpe).map { s =>
      val symbolString = s.name.decodedName.toString
      q"${TermName(symbolString)} = ${handlePath(c)(s.typeSignature, symbolString)}"
    }

    q"(c : Config) => ${TermName(tpe.typeSymbol.name.decodedName.toString)}(..$params)"
  }

  def getFromConfigImpl[T: c.WeakTypeTag](c: Context): c.Expr[Config => T] = {
    c.Expr[Config => T](handleCaseClass(c)(c.weakTypeOf[T]))
  }
}
