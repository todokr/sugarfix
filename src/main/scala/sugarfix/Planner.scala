package sugarfix

import grokschema.core.{Reference, ReferentTree, TableId}

trait Planner {

  def plan(focalTables: Seq[FocalTable], references: Set[Reference]): Seq[LogicalQuery] = {
    val trees = buildTree(focalTables, references)
    Seq()
  }

  private[sugarfix] def buildTree(focalTables: Seq[FocalTable], references: Set[Reference]): Seq[ReferentTree] = {
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
