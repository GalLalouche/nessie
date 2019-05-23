package com.nessie.model.map

sealed trait Direction

object Direction {
  val values: Iterable[Direction] = Vector(Up, Down, Left, Right)
  case object Up extends Direction
  case object Down extends Direction
  case object Left extends Direction
  case object Right extends Direction

  def from(mp1: MapPoint, mp2: MapPoint): Option[Direction] =
    if (mp1.manhattanDistanceTo(mp2) != 1) None else Some {
      mp1.y - mp2.y match {
        case -1 => Direction.Down
        case 0 => mp1.x - mp2.x match {
          case -1 => Direction.Right
          case 1 => Direction.Left
        }
        case 1 => Direction.Up
      }
    }
}
