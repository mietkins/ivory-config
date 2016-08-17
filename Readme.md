# Welocme
This is SIMPLE mapper from com.typesafe.config.Config to case class powered by scala macros.

# Installl 

For sbt users

1. Clone this repository
2. Run command sbt clean publishLocal inside repository dir
3. Add dependency to your project "pl.mietkins" %% "ivory-config" % "0.1"
4. Use them :)

# Example

```scala
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

```

For more examples see [Tests](src/test/scala/pl/mietkins/ivory/config/ImplTest.scala)
