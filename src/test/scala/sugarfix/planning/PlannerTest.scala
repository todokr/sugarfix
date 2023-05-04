package sugarfix.planning

import grokschema.core.{Reference, ReferentTree, TableId}
import munit.FunSuite
import sugarfix.dsl.FocalTable
import sugarfix.dsl.FocalTable._
import SpecifiedValue._

class PlannerTest extends FunSuite {
  val planner = new Planner {}

  test("Planner#buildTree") {
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
        fromColumn = "country_id",
        toTable = TableId("public", "country"),
        toColumn = "country_id"
      )
    )

    val result = planner.buildTree(tables, references)

    val expected = Seq(
      ReferentTree(
        Set(
          Reference(
            constraintName = "fk_city",
            fromTable = TableId("public", "city"),
            fromColumn = "country_id",
            toTable = TableId("public", "country"),
            toColumn = "country_id"
          )
        ),
        TableId("public", "city")
      )
    )

    assertEquals(result, expected)
  }
}
