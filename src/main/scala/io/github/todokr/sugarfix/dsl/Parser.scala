package io.github.todokr.sugarfix.dsl

import fastparse.Parsed
import io.github.todokr.sugarfix.{Column, FocalTable, Row, SpecifiedValue}


trait Parser {
  import scala.util.chaining._
  import fastparse.{parse => fParse}
  import Parser._

  def parseDsl(raw: String): Seq[FocalTable] =
    raw.pipe(normalize).pipe(parse)

  private def normalize(input: String): String =
    input.stripMargin.linesIterator.map(_.trim).filter(_.nonEmpty).mkString(Newline)

  private def parse(input: String): Seq[FocalTable] =
    fParse(input, DslExpr(_)) match {
      case Parsed.Success(value, _) => value
      case failure: Parsed.Failure => throw new Exception(failure.toString)
    }

}

private object Parser {
  import fastparse._
  import NoWhitespace._

  val Newline: String = "\n"

  def DslExpr[_: P]: P[Seq[FocalTable]] = P(FocalTableExpr.rep)

  def FocalTableExpr[_: P]: P[FocalTable] =
    P(TableName ~ UpperLine ~ HeaderLine ~ DelimiterLine ~ ContentLines ~ LowerLine).map {
      case (tableName, headers, contents) =>
        val rows = contents.map { values =>
          val cols = headers.zip(values).map { case (header, value) => Column(header, SpecifiedValue(value)) }
          Row(cols)
        }
        FocalTable(tableName, rows)
    }

  def TableName[_: P]: P[String] = P("#" ~ WSs ~ Term.! ~ Newline)
  def HeaderLine[_: P]: P[Seq[String]] = P(("│" ~ WSs ~ Term.!).rep(1) ~ "│" ~ Newline).map(_.map(_.trim))
  def ContentLines[_: P]: P[Seq[Seq[String]]] = P(ContentLine.rep(1))
  def ContentLine[_: P]: P[Seq[String]] = P(("│" ~ WSs ~ Term.!).rep(1) ~ "│" ~ Newline).map(_.map(_.trim))
  def UpperLine[_: P]: P[Unit] = P("┌" ~ ("─" | "┬").rep ~ "┐" ~ Newline)
  def DelimiterLine[_: P]: P[Unit] = P("├" ~ ("─" | "┼").rep ~ "┤" ~ Newline)
  def LowerLine[_: P]: P[Unit] = P("└" ~ ("─" | "┴").rep ~ "┘" ~ (Newline | ""))

  def WS[_: P] = P(" ")
  def WSs[_: P] = P(WS.rep)
  def Alpha[_: P] = P(CharIn("A-z").rep(1))
  def Number[_: P] = P(CharIn("0-9").rep(1))
  def AlNum[_: P] = P((Number | Alpha).rep(1))
  def Term[_: P] = P(AlNum ~ (AlNum | WS).rep)
}

