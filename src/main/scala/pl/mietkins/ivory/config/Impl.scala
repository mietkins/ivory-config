package pl.mietkins.ivory.config

import com.typesafe.config.{Config, ConfigValue}

import scala.reflect.macros.blackbox.Context

object Impl {

  private[config] def handleBoolean(c: Context): c.Tree = {
    import c.universe._

    q"(cv : com.typesafe.config.ConfigValue) => cv.unwrapped().asInstanceOf[Boolean]"
  }

  private[config] def handleString(c: Context): c.Tree = {
    import c.universe._
    q"(cv : ConfigValue) => cv.unwrapped().asInstanceOf[String]"
  }

  private[config] def handleNumber(c: Context)(tpe: c.Type): c.Tree = {

    import c.universe._

    val name = tpe.typeSymbol.name.decodedName.toString.toLowerCase() + "Value"

    q"(cv : ConfigValue) => cv.unwrapped().asInstanceOf[java.lang.Number].${TermName(name)}"
  }

  private[config] def handleOption(c: Context)(tpe: c.Type): c.Tree = {
    import c.universe._

    val headType = tpe.typeArgs.head

    q"(cv : ConfigValue) => Some(${handleConfig(c)(headType)}(cv))"
  }

  private[config] def handleList(c: Context)(tpe: c.Type): c.Tree = {
    import c.universe._

    val headType = tpe.typeArgs.head

    q"""(cv : ConfigValue) =>
       cv.asInstanceOf[ConfigList].map(e => ${handleConfig(c)(headType)}(e)).toList"""
  }

  private[config] def handleMap(c: Context)(tpe: c.Type): c.Tree = {
    import c.universe._

    val headType = tpe.typeArgs.tail.head

    q"""(cv : ConfigValue) =>
       cv.asInstanceOf[ConfigObject].entrySet()
       .map(e => e.getKey() -> ${handleConfig(c)(headType)}(e.getValue())).toMap"""
  }

  private[config] def handleCaseClass(c: Context)(tpe: c.Type): c.Tree = {

    import c.universe._


    val params = Core.getCaseClassAccessors(c)(tpe).map { s =>
      val symbolString = s.name.decodedName.toString

      val tpe = s.typeSignature.resultType

      val result = if(tpe <:< c.typeOf[Option[_]]) {
        q"if(configObject.contains(${symbolString})) ${handleConfig(c)(tpe)}(configObject.get(${symbolString})) else None"
      } else {
        q"${handleConfig(c)(tpe)}(configObject.get(${symbolString}))"
      }

      q"${TermName(symbolString)} = ${result}"
    }

    q"""(cv : ConfigValue) => {
       val configObject = cv.asInstanceOf[ConfigObject]
       ${TermName(tpe.typeSymbol.name.decodedName.toString)}(..$params)}"""
  }

  // scalastyle:off
  private[config] def handleConfig(c: Context)(tpe: c.Type): c.Tree = {

    tpe.resultType match {
      case t if t =:= c.typeOf[String] => handleString(c)
      case t if t <:< c.typeOf[Boolean] => handleBoolean(c)
      case t if t <:< c.typeOf[Int] || t <:< c.typeOf[Long] || t <:< c.typeOf[Double] => handleNumber(c)(t)
      case t if t <:< c.typeOf[Option[_]] => handleOption(c)(t)
      case t if t <:< c.typeOf[List[_]] => handleList(c)(t)
      case t if t <:< c.typeOf[Map[String, _]] => handleMap(c)(t)
      case t if Core.isCaseClass(c)(t.typeSymbol) => handleCaseClass(c)(t)
      case t => throw new NotImplementedError(s"$t \n ${Core.supportedTypesString}")
    }
  }

  def getFromConfigImpl[T: c.WeakTypeTag](c: Context): c.Expr[ConfigValue => T] = {
    import c.universe._
    c.Expr[ConfigValue => T](
      q"""
         import scala.collection.JavaConversions._
         import com.typesafe.config.{ConfigValue, ConfigObject, ConfigList}
         ${handleConfig(c)(c.weakTypeOf[T])}
        """)
  }

}
