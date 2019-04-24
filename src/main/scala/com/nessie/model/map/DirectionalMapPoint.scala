package com.nessie.model.map

import com.nessie.model.map.Direction._

// Not a case class, since the default apply method is overriden.
final class DirectionalMapPoint private(val x: Int, val y: Int, val direction: Direction) {
  require(y >= 0)
  require(x >= 0)
  require(direction != null)

  val toPoint = MapPoint(x, y)

  private def canonical(ps: (MapPoint, MapPoint)): (MapPoint, MapPoint) =
    if (ps._1.x < ps._2.x || ps._1.x == ps._2.x && ps._1.y < ps._2.y) ps else ps.swap
  def points: (MapPoint, MapPoint) = canonical(toPoint -> (direction match {
    case Up => MapPoint(x, y - 1)
    case Down => MapPoint(x, y + 1)
    case Left => MapPoint(x - 1, y)
    case Right => MapPoint(x + 1, y)
  }))
  def canEqual(other: Any): Boolean = other.isInstanceOf[DirectionalMapPoint]
  override def equals(other: Any): Boolean = other match {
    case that: DirectionalMapPoint =>
      (that canEqual this) &&
          x == that.x &&
          y == that.y &&
          direction == that.direction
    case _ => false
  }
  override def hashCode(): Int = {
    val state = Seq(x, y, direction)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
  override def toString = s"DirectionalMapPoint($x, $y, $direction)"
}

object DirectionalMapPoint {
  def around(p: MapPoint): Iterable[DirectionalMapPoint] = Direction.values.map(DirectionalMapPoint(p, _))

  def apply(p: MapPoint, d: Direction): DirectionalMapPoint = apply(p.x, p.y, d)
  def apply(x: Int, y: Int, d: Direction): DirectionalMapPoint =
    if (d == Down ||
        d == Right ||
        d == Up && y == 0 ||
        d == Left && x == 0)
      new DirectionalMapPoint(x, y, d)
    else d match {
      case Up => new DirectionalMapPoint(x, y - 1, Down)
      case Left => new DirectionalMapPoint(x - 1, y, Right)
    }

  def unapply(arg: DirectionalMapPoint): Option[(Int, Int, Direction)] = Some((arg.x, arg.y, arg.direction))
  def between(p1: MapPoint, p2: MapPoint): DirectionalMapPoint = {
    require(p1.manhattanDistanceTo(p2) == 1)
    DirectionalMapPoint(p1,
      if (p1.x == p2.x + 1) Left
      else if (p1.x == p2.x - 1) Right
      else if (p1.y == p2.y + 1) Up
      else {
        assert(p1.y == p2.y - 1)
        Down
      }
    )
  }
}
