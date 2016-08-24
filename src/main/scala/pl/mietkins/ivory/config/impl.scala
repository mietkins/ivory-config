package pl.mietkins.ivory.config

import com.typesafe.config.Config

import scala.reflect.macros.blackbox.Context

object impl {

  def isCaseClass(c: Context)(symbol: c.Symbol): Boolean = symbol.isClass && symbol.asClass.isCaseClass

  def getCaseClassAccessors(c: Context)(t: c.Type): List[c.Symbol] = {
    t.members.collect({
      case m: c.universe.MethodSymbol if m.isCaseAccessor => m
    }).toList
  }

  def handlePath(c: Context)(tpe: c.Type, path: String): c.Tree = {

    import c.universe._

    tpe.resultType match {
      case t if t <:< c.typeOf[AnyVal] || t =:= c.typeOf[String] => {
        val methodName = s"get${t.typeSymbol.name.decodedName.toString}"
        q"c.${TermName(methodName)}(${path})"
      }
      case t if t <:< c.typeOf[Option[_]] => {
        val innerTree = handlePath(c)(t.typeArgs.head, path)
        q"if(c.hasPath($path)) Some(${innerTree}) else None"
      }
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
      case t if isCaseClass(c)(t.typeSymbol) => q"${handleCaseClass(c)(t)}(c.getConfig(${path}))"
    }
  }

  def handleCaseClass(c: Context)(tpe: c.Type): c.Tree = {
    import c.universe._

    assert(isCaseClass(c)(tpe.typeSymbol))

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
