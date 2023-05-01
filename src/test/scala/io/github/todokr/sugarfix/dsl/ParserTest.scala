package io.github.todokr.sugarfix.dsl

import fastparse.{parse => fParse}
import io.github.todokr.sugarfix.SpecifiedValue._
import io.github.todokr.sugarfix._

class ParserTest extends munit.FunSuite {
  val parser = new Parser {}

  test("normalize") {
    val input =
      """
        |
        |  # departments
        |  ┌───────┬───────────────────────────┐
        |  │ alias │      department_name      │
        |  ├───────┼───────────────────────────┤
        |  │ hr    │ Human Resource Department │
        |  │ sales │ Sales Department          │
        |  └───────┴───────────────────────────┘
        |
        |  """

    val result = parser.normalize(input)
    val expected =
      """# departments
        |┌───────┬───────────────────────────┐
        |│ alias │      department_name      │
        |├───────┼───────────────────────────┤
        |│ hr    │ Human Resource Department │
        |│ sales │ Sales Department          │
        |└───────┴───────────────────────────┘""".stripMargin

    assertEquals(result, expected)
  }

  test("parse reference") {
    val input    = "departments -> hr"
    val result   = fParse(input, Parser.ReferenceTerm(_)).get.value
    val expected = Reference("departments", "hr")
    assertEquals(result, expected)
  }

  test("parseDsl") {
    val input =
      """# departments
        |┌───────┬───────────────────────────┐
        |│ alias │      department_name      │
        |├───────┼───────────────────────────┤
        |│ hr    │ Human Resource Department │
        |│ sales │ Sales Department          │
        |└───────┴───────────────────────────┘
        |
        |# users
        |┌──────────────┬───────────┬─────┬──────────────────────┐
        |│    alias     │ user_name │ age │    department_id     │
        |├──────────────┼───────────┼─────┼──────────────────────┤
        |│ hr_person    │ Jinnai    │  31 │ departments -> hr    │
        |│ sales_person │ Urita     │  27 │ departments -> sales │
        |└──────────────┴───────────┴─────┴──────────────────────┘""".stripMargin

    val result = parser.parseDsl(input)
    val expected = Seq(
      FocalTable(
        tableName = "departments",
        rows = Seq(
          Row(
            Seq(
              Column("alias", Text("hr")),
              Column("department_name", Text("Human Resource Department"))
            )
          ),
          Row(
            Seq(
              Column("alias", Text("sales")),
              Column("department_name", Text("Sales Department"))
            )
          )
        )
      ),
      FocalTable(
        tableName = "users",
        rows = Seq(
          Row(
            Seq(
              Column("alias", Text("hr_person")),
              Column("user_name", Text("Jinnai")),
              Column("age", Text("31")),
              Column("department_id", Reference("departments", "hr"))
            )
          ),
          Row(
            Seq(
              Column("alias", Text("sales_person")),
              Column("user_name", Text("Urita")),
              Column("age", Text("27")),
              Column("department_id", Reference("departments", "sales"))
            )
          )
        )
      )
    )
    assertEquals(result, expected)
  }

}
