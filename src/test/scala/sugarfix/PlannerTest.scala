package sugarfix

import grokschema.core.{Reference, TableId}
import munit.FunSuite
import sugarfix.SpecifiedValue.{External, Text}

class PlannerTest extends FunSuite {
  val planner = new Planner {}

  test("plan") {
    val tables = Seq(
      FocalTable(
        tableName = "city",
        rows = Seq(
          Row(
            Seq(
              Column("city", Text("Tokyo")),
              Column("last_updated", Text("2023-05-02 07:39:07.975863 +00:00"))
            )
          )
        )
      )
    )
    val references = Set(
      Reference(
        constraintName = "fk_city",
        fromTable = TableId("public", "city"),
        fromColumn= "country_id",
        toTable = TableId("public", "country"),
        toColumn = "country_id"
      )
    )

    val result = planner.plan(tables, references)

    val expected = Seq(LogicalQuery(
      schema = "public",
      tableName = "country",
      colValues = Seq()
    ))

    assertEquals(result, expected)
  }
}
