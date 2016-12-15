package com.nessie.model.map

/** Thrown when someone attempting access a point that is empty */
class MapEmptyException(val p: MapPoint) extends RuntimeException("Map is empty at " + p)
