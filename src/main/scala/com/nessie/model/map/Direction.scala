package com.nessie.model.map

sealed trait Direction

object Direction {
  val values: Iterable[Direction] = Vector(Up, Down, Left, Right)
  object Up extends Direction
  object Down extends Direction
  object Left extends Direction
  object Right extends Direction
}
