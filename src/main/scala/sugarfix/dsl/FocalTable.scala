package sugarfix.dsl

import FocalTable.Row

final case class FocalTable(tableName: String, rows: Seq[Row])

object FocalTable {
  final case class Row(columns: Seq[Column])
  final case class Column(name: String, value: SpecifiedValue)

  trait SpecifiedValue
  object SpecifiedValue {
    final case class Text(value: String) extends SpecifiedValue {
      override def toString: String = value
    }
    final case class External(tableName: String, alias: String) extends SpecifiedValue {
      override def toString: String = s"$tableName -> $alias"
    }
  }
}
