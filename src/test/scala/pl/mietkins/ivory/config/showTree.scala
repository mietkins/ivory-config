package pl.mietkins.ivory.config

import com.typesafe.config.Config

import scala.collection.JavaConversions._
import scala.language.experimental.macros

object showTree extends App {

  import examples.example1._

  def getFromConfigTest[T]: Config => T = macro testImpl.getFromConfigImplTest[T]

  getFromConfigTest[Person]
}
