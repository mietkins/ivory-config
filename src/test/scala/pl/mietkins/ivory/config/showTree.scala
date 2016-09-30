package pl.mietkins.ivory.config

import com.typesafe.config.ConfigValue

import scala.language.experimental.macros

object showTree extends App {

  import examples.Example1._

  def getFromConfigTest[T]: ConfigValue => T = macro TestImpl.getFromConfigImplTest[T]

  getFromConfigTest[Person]
}
