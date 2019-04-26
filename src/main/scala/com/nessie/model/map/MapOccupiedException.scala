package com.nessie.model.map

class MapOccupiedException(p: MapPoint) extends RuntimeException("Map is occupied on " + p)
