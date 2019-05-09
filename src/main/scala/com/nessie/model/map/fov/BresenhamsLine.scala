package com.nessie.model.map.fov

import com.nessie.model.map.MapPoint

import scala.collection.AbstractIterator

/** See [[https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm]] */
private object BresenhamsLine {
  /** Returns all the points crossed by the straight line from p1 to p2. */
  def apply(p0: MapPoint, p1: MapPoint): Iterable[MapPoint] = {
    // Modified implementation from https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Scala
    val x0 = p0.x
    val y0 = p0.y
    val x1 = p1.x
    val y1 = p1.y

    val dx = math.abs(x1 - x0)
    val sx = if (x0 < x1) 1 else -1
    val dy = math.abs(y1 - y0)
    val sy = if (y0 < y1) 1 else -1

    // Since the map is rectangular, all the points between p0 and p1 are always in the map bounds.
    new AbstractIterator[(Int, Int)] {
      private var x = x0
      private var y = y0
      private var err = (if (dx > dy) dx else -dy) / 2
      def next = {
        val $ = (x, y)
        val e2 = err
        if (e2 > -dx) {err -= dy; x += sx}
        if (e2 < dy) {err += dx; y += sy}
        $
      }
      def hasNext = sx * x <= sx * x1 && sy * y <= sy * y1
    }.map(MapPoint.apply).toVector
  }
}
