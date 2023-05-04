package sugarfix.planning

final case class LogicalQuery(schema: String, tableName: String, colValues: Seq[ColumnValue])

sealed trait ColumnValue {
  def columnName: String
  def toString: String
}

object ColumnValue {
  final case class TextValue(columnName: String, value: String) extends ColumnValue {
    override def toString: String = value
  }
  // TODO other type
}
