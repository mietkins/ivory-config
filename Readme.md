## Welcome 
This is SIMPLE automapper from com.typesafe.config.Config to case class powered by scala macros.

Supported types:

    T ∈ [case class, Map[String, T] List[T], Option[T], String, Boolean, Int, Long, Double]

## Build status
[![Build Status](https://travis-ci.org/mietkins/ivory-config.svg?branch=master)](https://travis-ci.org/mietkins/ivory-config)

## Install

Add dependency

    libraryDependencies += "pl.mietkins" %% "ivory-config" % "0.2.0"

## Example

```scala
import com.typesafe.config.{ConfigFactory}
import pl.mietkins.ivory.config._

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
