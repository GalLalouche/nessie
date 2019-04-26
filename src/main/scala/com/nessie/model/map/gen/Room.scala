package com.nessie.model.map.gen

import com.nessie.model.map.MapPoint

private case class Room(x: Int, y: Int, w: Int, h: Int) {
  def pointInRectangle(p: MapPoint): Boolean = p.x >= x && p.x < x + w && p.y >= y && p.y < y + h
  // TODO implement more efficiently?
  def isOverlapping(room: Room): Boolean = room.mapPoints.exists(pointInRectangle)
  private def perimeterRoom = Room(x - 1, y - 1, w + 2, h + 2)
  def isAdjacent(room: Room): Boolean = perimeterRoom.isOverlapping(room)

  def mapPoints: Iterable[MapPoint] =
    for (x <- this.x until this.x + w; y <- this.y until this.y + h) yield MapPoint(x, y)
}
