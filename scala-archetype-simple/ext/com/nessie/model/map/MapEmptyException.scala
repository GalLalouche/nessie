package com.nessie.model.map

class MapEmptyException(p: MapPoint) extends RuntimeException("Map is empty at " + p) {}