package com.nessie.model.map

case class MapPoint(x: Int, y: Int) {
  require(x >= 0)
  require(y >= 0)

  def manhattanDistanceTo(unitLocation: MapPoint): Int =
    Math.abs(x - unitLocation.x) + Math.abs(y - unitLocation.y)

  def go(d: Direction): MapPoint = d match {
    case Direction.UP => MapPoint(x, y - 1)
    case Direction.DOWN => MapPoint(x, y + 1)
    case Direction.LEFT => MapPoint(x - 1, y)
    case Direction.RIGHT => MapPoint(x + 1, y)
  }
}
