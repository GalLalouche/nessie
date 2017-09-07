package com.nessie.model.map

// Not a case class, since the default apply method is overriden.
final class DirectionalMapPoint private(val x: Int, val y: Int, val direction: Direction) {
  require(y >= 0)
  require(x >= 0)
  require(direction != null)

  val toPoint = MapPoint(x, y)

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
  def apply(p: MapPoint, d: Direction): DirectionalMapPoint = apply(p.x, p.y, d)
  def apply(x: Int, y: Int, d: Direction): DirectionalMapPoint =
    if (d == Direction.Down ||
        d == Direction.Right ||
        d == Direction.Up && y == 0 ||
        d == Direction.Left && x == 0)
      new DirectionalMapPoint(x, y, d)
    else d match {
      case Direction.Up => new DirectionalMapPoint(x, y - 1, Direction.Down)
      case Direction.Left => new DirectionalMapPoint(x - 1, y, Direction.Right)
    }

  def unapply(arg: DirectionalMapPoint): Option[(Int, Int, Direction)] = Some((arg.x, arg.y, arg.direction))
}
