package sugarfix

final case class FocalTable(tableName: String, rows: Seq[Row])
final case class Row(columns: Seq[Column])
final case class Column(name: String, value: SpecifiedValue)

trait SpecifiedValue
object SpecifiedValue {
  final case class Text(value: String) extends SpecifiedValue {
    override def toString: String = value
  }
  final case class Reference(tableName: String, alias: String) extends SpecifiedValue {
    override def toString: String = s"$tableName -> $alias"
  }
}
