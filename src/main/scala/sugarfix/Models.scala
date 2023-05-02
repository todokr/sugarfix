package sugarfix

final case class FocalTable(tableName: String, rows: Seq[Row])
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

final case class LogicalQuery(schema: String, tableName: String, colValues: Seq[ColumnValue])
final case class ColumnValue(columnName: String, value: Value)
sealed trait Value {
  def toString: String
}
object Value {
  final case class TextValue(value: String) extends Value {
    override def toString: String = value
  }

  // TODO other type
}
