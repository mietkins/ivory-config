package pl.mietkins.ivory.config.examples

import com.typesafe.config.{ConfigFactory, Config}
import pl.mietkins.ivory.config.getFromConfig
import scala.collection.JavaConversions._

object example1 extends App {

  case class Pet(name : String)

  case class Hobby(name: String)

  case class Person(name: String, age: Long, hobbies: List[Hobby], pet : Option[Pet])

  val configString =
    """
    example1 = {
      name = "Olaf"
      age = 110
      hobbies = [
      {
        name = "yoga"
      }
      {
        name = "books"
      }]
    }
    """

  val config = ConfigFactory.parseString(configString)

  val person: Person = getFromConfig[Person](config.getConfig("example1"))

  println(person)
}
