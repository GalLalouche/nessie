package com.nessie.model.map.gen

import common.rich.primitives.RichBoolean._
import com.nessie.model.map.MapPoint

private case class Room(x: Int, y: Int, w: Int, h: Int) {
  private def topLeft = MapPoint(x, y)
  private def topRight = MapPoint(x + w, y)
  private def bottomLeft = MapPoint(x, y + h)
  private def bottomRight = MapPoint(x + w, y + h)
  private def points: Traversable[MapPoint] = Vector(topLeft, topRight, bottomLeft, bottomRight)
  def pointNotInRectangle(p: MapPoint): Boolean = p.x < x || p.x >= x + w || p.y < y || p.y >= y + h
  def pointInRectangle(p: MapPoint): Boolean = pointNotInRectangle(p).isFalse
  def noOverlap(room: Room): Boolean = room.points.forall(pointNotInRectangle)

  def mapPoints = for {
    x <- this.x until this.x + w
    y <- this.y until this.y + h
  } yield MapPoint(x, y)
}
