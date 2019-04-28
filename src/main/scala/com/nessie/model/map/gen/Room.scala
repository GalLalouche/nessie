package com.nessie.model.map.gen

import com.nessie.model.map.MapPoint

private case class Room(x: Int, y: Int, w: Int, h: Int) {
  def pointInRectangle(p: MapPoint): Boolean = p.x >= x && p.x < x + w && p.y >= y && p.y < y + h
  def isOverlapping(other: Room): Boolean =
    this.y <= other.y + other.h &&
        this.y + this.h > other.y &&
        this.x <= other.x + other.w &&
        this.x + this.w > other.x
  private def perimeterRoom = Room(x - 1, y - 1, w + 2, h + 2)
  def isAdjacent(room: Room): Boolean = perimeterRoom.isOverlapping(room)
}
