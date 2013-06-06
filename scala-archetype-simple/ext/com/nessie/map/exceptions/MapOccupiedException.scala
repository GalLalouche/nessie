package com.nessie.map.exceptions

import com.nessie.map.model.MapPoint

class MapOccupiedException(p: MapPoint) extends RuntimeException("Map is occupied at " + p)