package com.nessie.model.map

case class MapPoint(x: Int, y: Int) {
  require(x >= 0)
  require(y >= 0)

  def manhattanDistanceTo(unitLocation: MapPoint): Int =
    Math.abs(x - unitLocation.x) + Math.abs(y - unitLocation.y)

  def go(d: Direction): MapPoint = d match {
    case Direction.Up => MapPoint(x, y - 1)
    case Direction.Down => MapPoint(x, y + 1)
    case Direction.Left => MapPoint(x - 1, y)
    case Direction.Right => MapPoint(x + 1, y)
  }
}