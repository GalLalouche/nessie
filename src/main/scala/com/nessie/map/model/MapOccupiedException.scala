package com.nessie.map.model

/** Thrown when trying to place a unit in a non-empty square */
class MapOccupiedException(val p: MapPoint) extends RuntimeException("Map is occupied at " + p)
