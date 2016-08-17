package pl.mietkins.ivory.config

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.FunSuite
import scala.collection.JavaConversions._

class ImplTest extends FunSuite {

  test("AnyVal mappings") {

    case class AnyValTest(bool: Boolean, int: Int, long: Long, double: Double)

    val config = ConfigFactory.parseString(
      """{
        |   bool = false
        |   int = 1
        |   long = 2
        |   double = 3
        |} """.stripMargin)

    assert(getFromConfig[AnyValTest](config) === AnyValTest(false, 1, 2, 3))
  }

  test("String mappings") {

    case class StringTest(string: String)

    val config = ConfigFactory.parseString(
      """{
        |   string = "Tested"
        |} """.stripMargin)

    assert(getFromConfig[StringTest](config) === StringTest("Tested"))
  }

  test("Nested case class") {
    case class InnerTest(string: String)
    case class OuterTest(inner: InnerTest)

    val config = ConfigFactory.parseString(
      """{
        |   inner = {
        |     string = "Tested"
        |   }
        |} """.stripMargin)

    assert(getFromConfig[OuterTest](config) === OuterTest(InnerTest("Tested")))
  }

  test("List mappings") {

    case class StringTest(string: String)

    case class ListTest(
      stringList: List[String],
      boolList: List[Boolean],
      intList: List[Int],
      longList: List[Long],
      doubleList: List[Double],
      caseClassList: List[StringTest])


    val config = ConfigFactory.parseString(
      """{
        |   stringList = ["Tested1", "Tested2"]
        |   boolList = [false, true, false]
        |   intList = [1, 2, 3]
        |   longList = [4, 5, 6]
        |   doubleList = [7, 8, 9]
        |   caseClassList = [
        |     {
        |       string = "CaseClass1"
        |     }
        |     {
        |       string = "CaseClass2"
        |     }
        |     {
        |       string = "CaseClass3"
        |     }
        |   ]
        |} """.stripMargin)

    assert(getFromConfig[ListTest](config) === ListTest(
      List("Tested1", "Tested2"),
      List(false, true, false),
      List(1, 2, 3),
      List(4l, 5l, 6l),
      List(7d, 8d, 9d),
      List(StringTest("CaseClass1"), StringTest("CaseClass2"), StringTest("CaseClass3"))))
  }

  test("Option mappings") {

    case class InnerTest(string: String)
    case class OptionTest(
      string: Option[String],
      bool: Option[Boolean],
      int: Option[Int],
      long: Option[Long],
      double: Option[Double],
      list: Option[List[Int]],
      inner: Option[InnerTest]
    )

    val emptyConfig = ConfigFactory.parseString("")
    val config = ConfigFactory.parseString(
      """
        | string = "Tested"
        | bool = true
        | int = 1,
        | long = 2,
        | double = 3,
        | list = [1, 2, 3,],
        | inner = { string = "Tested" }
      """.stripMargin)


    assert(getFromConfig[OptionTest](emptyConfig) === OptionTest(None, None, None, None, None, None, None))
    assert(getFromConfig[OptionTest](config) === OptionTest(
      Some("Tested"),
      Some(true),
      Some(1),
      Some(2l),
      Some(3d),
      Some(List(1, 2, 3)),
      Some(InnerTest("Tested"))))
  }
}
