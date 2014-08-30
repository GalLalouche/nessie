package com.nessie.map.exceptions

import com.nessie.map.model.MapPoint

/**
 * Thrown when someone is trying to access a point that is empty 
 */
class MapEmptyException(val p: MapPoint) extends RuntimeException("Map is empty at " + p)