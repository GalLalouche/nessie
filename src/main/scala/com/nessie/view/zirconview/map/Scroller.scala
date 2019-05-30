package com.nessie.view.zirconview.map

import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.collections.RichIterator._
import common.rich.RichT._

private class Scroller(properties: ScrollableMapViewProperties) {
  private val graphicsSize = properties.graphicsSize
  type Offset = MapPoint
  /** Can return none if no scrolling is needed. */
  def apply(n: Int, direction: Direction): Option[Offset] = {
    val gs = properties.getCurrentMapSize
    def offsetInBounds(mp: MapPoint): Boolean =
      mp.x >= 0 && mp.y >= 0 &&
          mp.x + graphicsSize.width <= gs.width && mp.y + graphicsSize.getHeight <= gs.height
    Iterator.iterate(properties.getCurrentOffset)(_ go direction)
        .slice(1, n + 1)
        .takeWhile(offsetInBounds)
        .lastOption
  }

  /**
   * Returns the offset that places the point as close the center as possible. Note that it isn't always
   * possible to center a point (e.g., corners), but it is always guaranteed that the point will be in view
   * when the offset is changed.
   */
  def center(mp: MapPoint, center: MapPoint): Offset = {
    val DistanceFromCenter(dx, dy) = DistanceFromCenter(center)(mp)
    val offset = properties.getCurrentOffset
    MapPoint(
      (offset.x + dx).coerceIn(0, properties.getCurrentMapSize.width - graphicsSize.width),
      (offset.y + dy).coerceIn(0, properties.getCurrentMapSize.height - graphicsSize.height),
    )
  }
}
