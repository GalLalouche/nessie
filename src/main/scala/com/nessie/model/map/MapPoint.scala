package com.nessie.model.map

import com.nessie.common.graph.Metric

import scala.annotation.tailrec

import common.rich.primitives.RichDouble._

final case class MapPoint(x: Int, y: Int) {
  def euclideanDistanceTo(p: MapPoint): Double = ((x - p.x).sq + (y - p.y).sq).sqrt

  def manhattanDistanceTo(unitLocation: MapPoint): Int =
    Math.abs(x - unitLocation.x) + Math.abs(y - unitLocation.y)

  def go(d: Direction): MapPoint = d match {
    case Direction.Up => MapPoint(x, y - 1)
    case Direction.Down => MapPoint(x, y + 1)
    case Direction.Left => MapPoint(x - 1, y)
    case Direction.Right => MapPoint(x + 1, y)
  }
  @tailrec def go(d: Direction, amount: Int): MapPoint =
    if (amount == 0) this else go(d).go(d, amount - 1)

  // Opt for performance over pretty code
  lazy val neighbors: Iterable[MapPoint] = Array((x, y - 1), (x, y + 1), (x - 1, y), (x + 1, y))
      .filter(MapPoint.isValid).map(MapPoint.apply).toVector
  lazy val neighborsAndDiagonals: Iterable[MapPoint] = Array(
    (x, y - 1), (x, y + 1), (x - 1, y), (x + 1, y),
    (x - 1, y - 1), (x + 1, y + 1), (x - 1, y + 1), (x + 1, y - 1),
  ).filter(MapPoint.isValid).map(MapPoint.apply).toVector
}

object MapPoint {
  private def isValid(e: (Int, Int)): Boolean = e._1 >= 0 && e._2 >= 0
  def apply(t: (Int, Int)): MapPoint = MapPoint(t._1, t._2)

  implicit val OrderingEv: Ordering[MapPoint] = Ordering.by(e => e.x -> e.y)
  implicit object MetricEv extends Metric[MapPoint] {
    override def distance(a1: MapPoint, a2: MapPoint): Double = a1.manhattanDistanceTo(a2)
  }
}
