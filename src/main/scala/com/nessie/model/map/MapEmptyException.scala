package com.nessie.model.map

/** Thrown when someone attempting access a point that is empty */
class MapEmptyException private(msg: String) extends RuntimeException(msg) {
  def this(p: MapPoint) = this("Map is empty on " + p)
  def this(pd: DirectionalMapPoint) = this("Map is empty on " + pd)
}

