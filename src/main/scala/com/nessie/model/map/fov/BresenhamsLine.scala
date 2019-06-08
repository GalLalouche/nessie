package com.nessie.model.map.fov

import com.nessie.model.map.{Direction, MapPoint}

import scala.collection.AbstractIterator

/** See [[https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm]] */
object BresenhamsLine {
  private def lineIterator(p0: MapPoint, p1: MapPoint): Iterator[MapPoint] = {
    if (p0 == p1)
      return Iterator(p0)
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
    }.map(MapPoint.apply)
  }
  /** Returns all the points crossed by the straight line from p1 to p2. */
  def apply(p0: MapPoint, p1: MapPoint): Iterable[MapPoint] = lineIterator(p0, p1).toVector

  /**
   * Bresenham's algorithm is an OK approximation for line of sight, because photons are like, really small.
   * But for movement, one cannot run diagonally from one cell to another if there isn't enough space.
   * For example, suppose you have the following map, where you wish to get from S to T.
   * *****--T
   * S-----**
   * It's not possible to run in a straight line from S to T due to the walls.
   *
   * This method therefore returns therefore returns all the cells that are necessary to be empty for such a
   * movement, i.e., it tries to paint a "thick" line.
   */
  def thick(p0: MapPoint, p1: MapPoint): Iterable[MapPoint] = {
    if (p0.x == p1.x || p0.y == p1.y)
      return apply(p0, p1) // If there is a clear veritcal or horizontal line from p0 to p1, take it.

    // As an approximation, we take the regular Bresenham's line, as well two additional lines, referred to in
    // the code as "shadows". The y-shadow is start vertical to p0 and ends horizontal to p1. The x-shadow
    // starts horizontal to p0 and ends vertical to p0. Both shadows are between p0 and p1.
    // For example, if we want to draw a line from (0, 3) to (6, 0), the y-shadow will be
    // from (0, 2) to (5, 0), and the x-shadow will be from (1, 3) to (6, 1).
    val yShadow = lineIterator(
      p0.go(if (p0.y < p1.y) Direction.Down else Direction.Up),
      p1.go(if (p0.x < p1.x) Direction.Left else Direction.Right),
    )
    val xShadow = lineIterator(
      p0.go(if (p0.x < p1.x) Direction.Right else Direction.Left),
      p1.go(if (p0.y < p1.y) Direction.Up else Direction.Down),
    )
    (lineIterator(p0, p1) ++ xShadow ++ yShadow).toSet.toVector.sorted
  }
}
