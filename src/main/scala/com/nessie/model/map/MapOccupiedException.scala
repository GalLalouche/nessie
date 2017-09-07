package com.nessie.model.map

/** Thrown when trying to place an item on an occupied slot */
class MapOccupiedException private(msg: String) extends RuntimeException(msg) {
  def this(p: MapPoint) = this("Map is occupied on " + p)
  def this(pd: DirectionalMapPoint) = this("Map is occupied on " + pd)
}
