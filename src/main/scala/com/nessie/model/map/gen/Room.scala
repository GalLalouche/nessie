package com.nessie.model.map.gen

import com.nessie.model.map.{DirectionalMapPoint, MapPoint}

private case class Room(x: Int, y: Int, w: Int, h: Int) {
  private def topLeft = MapPoint(x, y)
  private def topRight = MapPoint(x + w, y)
  private def bottomLeft = MapPoint(x, y + h)
  private def bottomRight = MapPoint(x + w, y + h)
  private def points: Traversable[MapPoint] = Vector(topLeft, topRight, bottomLeft, bottomRight)
  def pointNotInRectangle(p: MapPoint): Boolean = p.x < x || p.x >= x + w || p.y < y || p.y >= y + h
  def noOverlap(room: Room): Boolean = room.points.forall(pointNotInRectangle)
  import com.nessie.model.map.Direction._

  private val wallsByPoint = Map(
    topLeft -> (Up, Left),
    topRight -> (Up, Right),
    bottomLeft -> (Down, Left),
    bottomRight -> (Down, Right),
  )
  def walls(p: MapPoint): Iterable[DirectionalMapPoint] = {
    def ifWall(p: Int, p2: Int, dmp: DirectionalMapPoint) =
      if (p == p2) Vector(dmp) else Vector.empty
    if (pointNotInRectangle(p)) Nil
    else ifWall(p.x, x, DirectionalMapPoint(p, Left)) ++
        ifWall(p.x, x + w - 1, DirectionalMapPoint(p, Right)) ++
        ifWall(p.y, y, DirectionalMapPoint(p, Up)) ++
        ifWall(p.y, y + h - 1, DirectionalMapPoint(p, Down))
  }
}
