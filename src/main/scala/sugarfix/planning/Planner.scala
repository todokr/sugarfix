package sugarfix.planning

import grokschema.core._
import sugarfix.dsl.FocalTable

object Main {
  def main(args: Array[String]): Unit = {
    val config = Config(
      "org.postgresql.Driver",
      "jdbc:postgresql://localhost:5432/dvdrental",
      "postgres",
      "postgres"
    )

    val loader = new MetadataLoader(config)
    loader.loadReferences().foreach(println)
  }
}

trait Planner {
  // val loader: grokschema.core.MetadataLoader = ???

  private[planning] val buildTree: (Seq[FocalTable], Set[Reference]) => Seq[ReferentTree] =
    (focalTables, references) => {
      val tableIds = focalTables.map(t => TableId("public", t.tableName))
      tableIds.foldLeft(Seq.empty[ReferentTree]) { (acc, tableId) =>
        if (acc.exists(_.contains(tableId))) {
          acc
        } else {
          val newTree = ReferentTree(references, tableId)
          newTree +: acc
        }
      }
    }

}
