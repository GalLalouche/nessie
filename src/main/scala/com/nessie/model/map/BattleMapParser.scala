package com.nessie.model.map

import common.rich.collections.RichTraversableOnce._
import common.rich.primitives.RichBoolean._

import scalaz.{-\/, \/, \/-}

trait BattleMapParser {
  def parse(s: String): BattleMap
}

object BattleMapParser {
  type Width = Int
  type Height = Int
  private def lines(s: String): InvalidBattleMapStringException \/ Seq[String] = {
    require(s.nonEmpty)
    val seq = s.split("\n").toVector
    if (seq.hasSameValues(_.length).isFalse) -\/(new InvalidBattleMapStringException(s)) else \/-(seq)
  }
  class InvalidBattleMapStringException(map: String) extends Exception(s"Invalid BattleMap string:\n$map")

  def fromPoints(create: (Width, Height) => BattleMap): BattleMapParser = map => lines(map) match {
    case -\/(a) => throw a
    case \/-(lines) =>
      val width = lines.head.length
      val $ = create(width, lines.length)
      val points = (for {
        y <- lines.indices
        x <- lines(y).indices
      } yield MapPoint(x, y) -> lines(y)(x)).toMap
      $.foldPoints((map, mp) => map.place(mp, points(mp) match {
        case '*' => FullWall
        case '_' => EmptyMapObject
      }))
  }
}
