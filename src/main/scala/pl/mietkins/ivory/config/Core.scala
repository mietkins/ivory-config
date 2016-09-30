package pl.mietkins.ivory.config

import scala.reflect.macros.blackbox.Context

/**
  * Created by mietkins on 30.09.16.
  */
object Core {

  private[config] val supportedTypesString =
    """
    Supported types:
      T ∈ [case class, Map[String, T] List[T], Option[T], String, Boolean, Int, Long, Double]
    """

  private[config] def isCaseClass(c: Context)(symbol: c.Symbol): Boolean = symbol.isClass && symbol.asClass.isCaseClass

  private[config] def getCaseClassAccessors(c: Context)(tpe: c.Type): List[c.Symbol] = {
    tpe.members.collect({
      case m: c.universe.MethodSymbol if m.isCaseAccessor => m
    }).toList
  }

}
