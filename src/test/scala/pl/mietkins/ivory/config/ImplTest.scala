package pl.mietkins.ivory.config

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.FunSuite

/**
  * Created by mietkins on 30.09.16.
  */
class ImplTest extends FunSuite {

  test("string mapping") {

    val config: Config = ConfigFactory.parseString(
      """
        |   string = "s"
        | """.stripMargin)

    assert(getFromConfig[String](config.getValue("string")) === "s")
  }

  test("boolean mapping") {

    val config: Config = ConfigFactory.parseString(
      """
        |   bool = false
        | """.stripMargin)

    assert(getFromConfig[Boolean](config.getValue("bool")) === false)
  }

  test("number mapping") {

    val config: Config = ConfigFactory.parseString(
      """
        |   int = 1
        |   long = 2
        |   double = 3.0
        | """.stripMargin)

    assert(getFromConfig[Int](config.getValue("int")) === 1)
    assert(getFromConfig[Long](config.getValue("long")) === 2)
    assert(getFromConfig[Double](config.getValue("double")) === 3.0)
  }


  test("list mapping") {

    val config: Config = ConfigFactory.parseString(
      """
        |   list = [1,2,3]
        |   listList = [[1,2,3]]
        | """.stripMargin)

    assert(getFromConfig[List[Int]](config.getValue("list")) === List(1, 2, 3))
    assert(getFromConfig[List[List[Int]]](config.getValue("listList")) === List(List(1, 2, 3)))
  }

  test("map mapping") {

    val config: Config = ConfigFactory.parseString(
      """
        |   map = {a = 1, b = 2, c = 3}
        |   mapMap = {a = { a = 1}, b = {b = 2}}
        | """.stripMargin)

    assert(getFromConfig[Map[String, Int]](config.getValue("map")) === Map("a" -> 1, "b" -> 2, "c" -> 3))
    assert(
      getFromConfig[Map[String, Map[String, Int]]](config.getValue("mapMap")) === Map("a" -> Map("a" -> 1), "b" -> Map("b" -> 2)))
  }


  test("case class mapping") {
    case class InnerTest(string: String)
    case class OuterTest(inner: InnerTest)

    val config = ConfigFactory.parseString(
      """{
        |   inner = {
        |     string = "Tested"
        |   }
        |} """.stripMargin)

    assert(getFromConfig[OuterTest](config.root()) === OuterTest(InnerTest("Tested")))
  }

  test("complex mapping") {

    case class InnerTest(
      string: String,
      boolean: Boolean,
      number: Long,
      option: Option[String])

    case class OuterTest(
      inner: InnerTest,
      list: List[Int],
      map: Map[String, Int],
      listMap: List[Map[String, Boolean]],
      mapListMap: Map[String, List[Map[String, InnerTest]]])


    val config = ConfigFactory.parseString(
      """{
        |   inner = {
        |     string = "Tested"
        |     boolean = false
        |     number = 2
        |     option = "some"
        |   }
        |   list : [1,2,3]
        |   map : { a = 1 }
        |   listMap : [{a = true, b = false}]
        |   mapListMap : {
        |     a = [
        |       {
        |         i1 = {
        |           string = "Tested"
        |           boolean = false
        |           number = 2
        |           option = "some"
        |         },
        |         i2 = {
        |           string = "Tested"
        |           boolean = false
        |           number = 2
        |         }
        |       }
        |     ]
        |   }
        |} """.stripMargin)

    assert(getFromConfig[OuterTest](config.root()) === OuterTest(
      InnerTest("Tested", false, 2, Some("some")),
      List(1, 2, 3),
      Map("a" -> 1),
      List(Map("a" -> true, "b" -> false)),
      Map(
        "a" -> List(
          Map(
            "i1" -> InnerTest("Tested", false, 2, Some("some")),
            "i2" -> InnerTest("Tested", false, 2, None))))))

  }
}
