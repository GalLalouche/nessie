package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.model.map.MapPoint

private case class Room(x: Int, y: Int, w: Int, h: Int) {
  private val rightWall = x + w - 1
  private val leftWall = x
  private val topWall = y
  private val bottomWall = y + h - 1
  private val corners = Vector(
    MapPoint(leftWall, topWall),
    MapPoint(leftWall, bottomWall),
    MapPoint(rightWall, topWall),
    MapPoint(rightWall, bottomWall),
  )
  def mapPoints: Iterable[MapPoint] =
    for (x <- leftWall to rightWall; y <- topWall to bottomWall) yield MapPoint(x, y)
  private def intersectsY(p: MapPoint): Boolean = p.y >= topWall && p.y <= bottomWall
  private def intersectsX(p: MapPoint): Boolean = p.x >= leftWall && p.x <= rightWall
  def pointInRectangle(p: MapPoint): Boolean = intersectsX(p) && intersectsY(p)
  def isOverlapping(other: Room): Boolean =
    this.topWall <= other.bottomWall &&
        this.bottomWall >= other.topWall &&
        this.leftWall <= other.rightWall &&
        this.rightWall >= other.leftWall
  private def perimeterRoom = Room(x - 1, y - 1, w + 2, h + 2)
  def isAdjacent(room: Room): Boolean = perimeterRoom.isOverlapping(room)
  def distanceTo(p: MapPoint): Int = {
    // A true O(1) solution: If p intersects the Room's x, it is the minimum of distance from the top or
    // bottom edge. If p intersects the Room's y, it is the minimum of distance from the left or right edge.
    // Otherwise, it's the minimum distance from any corner. Of course, if p is in the room then the distance
    // is equal to 0.
    if (pointInRectangle(p))
      0
    else if (intersectsX(p))
      Math.min(Math.abs(p.y - topWall), Math.abs(p.y - bottomWall))
    else if (intersectsY(p))
      Math.min(Math.abs(p.x - leftWall), Math.abs(p.x - rightWall))
    else corners.view.map(p.manhattanDistanceTo).min
  }
}
