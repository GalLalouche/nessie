package com.nessie.map.exceptions

import com.nessie.map.model.MapPoint

class MapEmptyException(p: MapPoint) extends RuntimeException("Map is empty at " + p)