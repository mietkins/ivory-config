package pl.mietkins.ivory

import com.typesafe.config.Config
import scala.language.experimental.macros

package object config {

  def getFromConfig[T]: Config => T = macro impl.getFromConfigImpl[T]
}
