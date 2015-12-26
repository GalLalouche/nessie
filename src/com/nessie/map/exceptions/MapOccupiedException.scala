package com.nessie.map.exceptions

import com.nessie.map.model.MapPoint

/** Thrown when trying to place a unit in a non-empty square */
class MapOccupiedException(val p: MapPoint) extends RuntimeException("Map is occupied at " + p)
