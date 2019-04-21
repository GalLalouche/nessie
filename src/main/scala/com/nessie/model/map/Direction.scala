package com.nessie.model.map

sealed trait Direction

object Direction {
  val values: Iterable[Direction] = Vector(Up, Down, Left, Right)
  case object Up extends Direction
  case object Down extends Direction
  case object Left extends Direction
  case object Right extends Direction
}
