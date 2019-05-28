package com.nessie.view.zirconview.map

import com.nessie.model.map.{GridSize, MapPoint}
import com.nessie.model.map.Direction._
import common.AuxSpecs
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.Sizes
import org.scalatest.FreeSpec

class ScrollerTest extends FreeSpec with AuxSpecs {
  private val gridSize: GridSize = GridSize(10, 8)
  private val graphicsSize: Size = Sizes.create(3, 5)
  "Out of bounds returns None" in {
    Scroller(1, Left, MapPoint(0, 0), graphicsSize, gridSize) shouldReturn None
    Scroller(1, Up, MapPoint(0, 0), graphicsSize, gridSize) shouldReturn None
    Scroller(1, Down, MapPoint(0, 3), graphicsSize, gridSize) shouldReturn None
    Scroller(1, Right, MapPoint(7, 0), graphicsSize, gridSize) shouldReturn None
  }
  "n=1 Goes in direction" in {
    val point = MapPoint(1, 2)
    Scroller(1, Left, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(0, 2))
    Scroller(1, Up, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(1, 1))
    Scroller(1, Down, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(1, 3))
    Scroller(1, Right, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(2, 2))
  }
  "n=k goes to k if possible" in {
    val gridSize: GridSize = GridSize(100, 80)
    val graphicsSize: Size = Sizes.create(3, 5)
    val point = MapPoint(10, 20)
    Scroller(4, Left, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(6, 20))
    Scroller(4, Up, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(10, 16))
    Scroller(4, Down, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(10, 24))
    Scroller(4, Right, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(14, 20))
  }
  "Goes to maximum n if k is too large possible" in {
    val point = MapPoint(1, 2)
    Scroller(8, Left, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(0, 2))
    Scroller(8, Up, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(1, 0))
    Scroller(8, Down, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(1, 3))
    Scroller(8, Right, point, graphicsSize, gridSize) shouldReturn Some(MapPoint(7, 2))
  }
}
