package com.nessie.model.map.gen

import com.nessie.model.map.MapPoint
import common.rich.primitives.RichBoolean._

private case class Room(x: Int, y: Int, w: Int, h: Int) {
  def pointNotInRectangle(p: MapPoint): Boolean = pointInRectangle(p).isFalse
  def pointInRectangle(p: MapPoint): Boolean = p.x >= x && p.x < x + w && p.y >= y && p.y < y + h
  def noOverlap(room: Room): Boolean = room.mapPoints.forall(pointNotInRectangle)

  def mapPoints: Iterable[MapPoint] = for {
    x <- this.x until this.x + w
    y <- this.y until this.y + h
  } yield MapPoint(x, y)
}
