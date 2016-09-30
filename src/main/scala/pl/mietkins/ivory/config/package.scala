package pl.mietkins.ivory

import com.typesafe.config.{Config, ConfigValue}

import scala.language.experimental.macros

package object config {


  /** *
    * Macro expands given type T to function (c : ConfigValue) => T
    *
    * Supported types
    *   T ∈ [case class, Map[String, T] List[T], Option[T], String, Boolean, Int, Long, Double]
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
    * import com.typesafe.config.{ConfigList, ConfigObject, ConfigValue}
    *
    * import scala.collection.JavaConversions._;
    *
    * ((cv: ConfigValue) => {
    *    val configObject = cv.asInstanceOf[ConfigObject];
    *
    *    Person(
    *      pet = if (configObject.contains("pet"))
    *        ((cv: ConfigValue) => Some(((cv: ConfigValue) => {
    *          val configObject = cv.asInstanceOf[ConfigObject];
    *          Pet(name = ((cv: ConfigValue) => cv.unwrapped().asInstanceOf[String]) (configObject.get("name")))
    *        }) (cv))) (configObject.get("pet"))
    *      else None,
    *      hobbies = ((cv: ConfigValue) => cv.asInstanceOf[ConfigList].map(((e) => ((cv: ConfigValue) => {
    *        val configObject = cv.asInstanceOf[ConfigObject];
    *        Hobby(name = ((cv: ConfigValue) => cv.unwrapped().asInstanceOf[String]) (configObject.get("name")))
    *      }) (e))).toList) (configObject.get("hobbies")),
    *      age = ((cv: ConfigValue) => cv.unwrapped().asInstanceOf[java.lang.Number].longValue) (configObject.get("age")),
    *      name = ((cv: ConfigValue) => cv.unwrapped().asInstanceOf[String]) (configObject.get("name")))
    *  })
    *
    * </pre>
    *
    */
  def getFromConfig[T]: ConfigValue => T = macro Impl.getFromConfigImpl[T]

  implicit def toConfigValue(c : Config) : ConfigValue = c.root()
}
