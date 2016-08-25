package pl.mietkins.ivory

import com.typesafe.config.Config
import scala.language.experimental.macros

package object config {


  /** *
    * Macro expands given type T to function (c : Config) => T
    *
    * Supported types
    *   case class, List, Option, String, Boolean, Int, Long, Double
    *
    * Example:
    *
    * <pre>
    * case class Pet(name : String)
    *
    * case class Hobby(name: String)
    *
    * case class Person(name: String, age: Long, hobbies: List[Hobby], pet : Option[Pet])
    *
    * getFromConfig[Person]
    *
    * </pre>
    *
    * Produces
    *
    * <pre>
    *
    * ((c: Config) =>
    *   Person(
    *     pet = if (c.hasPath("pet")) Some(
    *       ((c: Config) => Pet(name = c.getString("name"))) (c.getConfig("pet")))
    *     else None,
    *     hobbies = c.getConfigList("hobbies").toList.map(((e) =>
    *       ((c: Config) => Hobby(name = c.getString("name"))) (e))),
    *     age = c.getLong("age"),
    *     name = c.getString("name")))
    *
    * </pre>
    *
    * Required imports
    *
    * <pre>
    * import com.typesafe.config.Config
    * import scala.collection.JavaConversions._
    * </pre>
    */

  def getFromConfig[T]: Config => T = macro impl.getFromConfigImpl[T]
}
