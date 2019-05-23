package com.nessie.model.map.gen.cellular_automata

import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.model.map.Direction._
import com.nessie.model.map.gen.cellular_automata.PathWidener.Width.TwoRightUp
import common.rich.RichT._
import enumeratum._

import scala.collection.immutable

private object PathWidener {
  sealed trait Width extends EnumEntry
  object Width extends Enum[Width] {
    val values: immutable.IndexedSeq[Width] = findValues
    case object One extends Width
    /** W is the widened path, the arrows are the original path, and S is the path's start
     *               WWWW
     *     →→→       W→→→
     *     ↑       WWW↑
     * S→→→↑   => S→→→↑
     */
    case object TwoRightUp extends Width
    /** W is the widened path, the arrows are the original path, and S is the path's start
     *     →→→        →→→
     *     ↑          ↑WW
     * S→→→↑   => S→→→↑W
     *             WWWWW
     */
    case object TwoLeftDown extends Width
    case object Three extends Width
  }

  private sealed trait MidPointProperty
  private case object Horizontal extends MidPointProperty
  private case object Vertical extends MidPointProperty
  private case object Corner extends MidPointProperty
  private def midPointProperty(p1: MapPoint, p2: MapPoint, p3: MapPoint): MidPointProperty =
    if (p1.y == p2.y && p1.y == p3.y) Horizontal else if (p1.x == p2.x && p1.x == p3.x) Vertical else Corner

  def apply(path: Seq[MapPoint], width: Width): Iterable[MapPoint] = {
    val indexedSeq = path.toIndexedSeq
    def endPoints(i: Int): Option[(MapPoint, MapPoint)] =
      if (i != 0 && i != indexedSeq.length - 1) None
      else if (i == 0) Some(indexedSeq(i) -> indexedSeq(i + 1))
      else Some(indexedSeq(i - 1) -> indexedSeq(i))
    width match {
      case Width.One => path
      case Width.TwoRightUp | Width.TwoLeftDown =>
        def wrapAngle(p: MapPoint, d1: Direction, d2: Direction) =
          Vector(p, p.go(d1), p.go(d2), p.go(d1).go(d2))
        val wrappingSide: Direction => Direction =
          if (width == TwoRightUp) {
            case Up => Left
            case Down => Right
            case Left => Down
            case Right => Up
          } else {
            case Down => Left
            case Up => Right
            case Right => Down
            case Left => Up
          }
        require(path.size >= 2)
        path.indices.flatMap(i => {
          val point = indexedSeq(i)
          endPoints(i) match {
            case Some((first, last)) => Vector(point, point.go(from(first, last).get |> wrappingSide))
            case None =>
              val before = indexedSeq(i - 1)
              val after = indexedSeq(i + 1)
              val (d1, d2) = from(before, point).get -> from(point, after).get
              midPointProperty(before, point, after) match {
                case Horizontal | Vertical => Vector(point, point.go(d1 |> wrappingSide))
                case Corner => wrapAngle(point, wrappingSide(d1), wrappingSide(d2))
              }
          }
        }
        ).toSet
      case Width.Three =>
        require(path.size >= 2)
        path.indices.flatMap(i => {
          val point = indexedSeq(i)
          endPoints(i) match {
            case Some((first, last)) =>
              if (first.y == last.y)
                Vector(point.go(Up), point, point.go(Down))
              else
                Vector(point.go(Left), point, point.go(Right))
            case None =>
              midPointProperty(indexedSeq(i - 1), point, indexedSeq(i + 1)) match {
                case Horizontal => Vector(point.go(Up), point, point.go(Down))
                case Vertical => Vector(point.go(Left), point, point.go(Right))
                case Corner => point.neighborsAndDiagonals.toVector :+ point
              }
          }
        }).toSet
    }
  }
}
