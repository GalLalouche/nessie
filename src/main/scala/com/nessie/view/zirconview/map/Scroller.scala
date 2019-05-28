package com.nessie.view.zirconview.map

import com.nessie.model.map.{Direction, GridSize, MapPoint}
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.collections.RichIterator._
import org.hexworks.zircon.api.data.Size

private object Scroller {
  def apply(
      n: Int, direction: Direction, currentOffset: MapPoint, graphicsSize: Size, gs: GridSize
  ): Option[MapPoint] = {
    def offsetInBounds(mp: MapPoint): Boolean =
      mp.x >= 0 && mp.y >= 0 &&
          mp.x + graphicsSize.width <= gs.width && mp.y + graphicsSize.getHeight <= gs.height
    Iterator.iterate(currentOffset)(_ go direction)
        .slice(1, n + 1)
        .takeWhile(offsetInBounds)
        .lastOption
  }
}
