package sugarfix

import grokschema.core.{Reference, ReferentTree, TableId}

trait Planner {

  def plan(focalTables: Seq[FocalTable], references: Set[Reference]): Seq[LogicalQuery] = {
    val tableIds = focalTables.map(t => TableId("public", t.tableName))
    val trees = ReferentTree(references, tableIds)
    trees.foreach(println)
    Seq()
  }
}
