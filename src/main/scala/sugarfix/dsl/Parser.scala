package sugarfix.dsl

import fastparse.Parsed

import FocalTable._
import SpecifiedValue._

trait Parser {
  import Parser._
  import fastparse.{parse => fParse}

  import scala.util.chaining._

  def parseDsl(raw: String): Seq[FocalTable] =
    raw.pipe(normalize).pipe(parse)

  private[dsl] def normalize(input: String): String =
    input.stripMargin.linesIterator.map(_.trim).filter(_.nonEmpty).mkString(Newline)

  private[dsl] def parse(input: String): Seq[FocalTable] =
    fParse(input, DslExpr(_)) match {
      case Parsed.Success(value, _) => value
      case failure: Parsed.Failure  => throw new Exception(failure.toString)
    }

}

private[dsl] object Parser {
  import fastparse._
  import NoWhitespace._

  val Newline: String = "\n"

  def DslExpr[$: P]: P[Seq[FocalTable]] = P(FocalTableExpr.rep)

  def FocalTableExpr[$: P]: P[FocalTable] =
    P(TableName ~ UpperLine ~ HeaderLine ~ DelimiterLine ~ ContentLines ~ LowerLine).map {
      case (tableName, headers, contents) =>
        val rows = contents.map { values =>
          val cols = headers.zip(values).map { case (header, value) => Column(header, value) }
          Row(cols)
        }
        FocalTable(tableName, rows)
    }

  def TableName[$: P]: P[String]       = P("#" ~ WSs ~ Term.! ~ Newline)
  def HeaderLine[$: P]: P[Seq[String]] = P(("│" ~ WSs ~ Term.!).rep(1) ~ "│" ~ Newline).map(_.map(_.trim))
  def ContentLines[$: P]: P[Seq[Seq[SpecifiedValue]]] = P((ContentLine ~ Newline.?).rep(1))
  def ContentLine[$: P]: P[Seq[SpecifiedValue]]       = P("│" ~ (WSs ~ (ReferenceTerm | TextTerm) ~ WSs ~ "│").rep)
  def UpperLine[$: P]: P[Unit]                        = P("┌" ~ ("─" | "┬").rep ~ "┐" ~ Newline)
  def DelimiterLine[$: P]: P[Unit]                    = P("├" ~ ("─" | "┼").rep ~ "┤" ~ Newline)
  def LowerLine[$: P]: P[Unit]                        = P("└" ~ ("─" | "┴").rep ~ "┘" ~ (Newline | ""))

  def ReferenceTerm[$: P]: P[SpecifiedValue] =
    P(Token.rep.! ~ WSs ~ "->" ~ WSs ~ Token.rep.!).map { case (t, a) => External(t, a) }
  def TextTerm[$: P]: P[SpecifiedValue] = P(Term.rep.!).map(x => Text(x.trim))

  def WS[$: P]     = P(" ")
  def WSs[$: P]    = P(WS.rep)
  def Alpha[$: P]  = P(CharIn("a-z", "A-Z").rep(1))
  def Number[$: P] = P(CharIn("0-9").rep(1))
  def Token[$: P]  = P((Number | Alpha | "_" | "-").rep(1))
  def Term[$: P]   = P(Token ~ (Token | WS).rep)
}
