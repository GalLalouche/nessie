package com.nessie.view.zirconview.map

import com.nessie.model.map.{GridSize, MapPoint}
import com.nessie.model.map.Direction._
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.data.Size
import org.scalatest.FreeSpec

import common.test.AuxSpecs

class ScrollerTest extends FreeSpec with AuxSpecs {
  private def withOffset(offset: MapPoint): Scroller = new Scroller(new ScrollableMapViewProperties {
    override def getCurrentMapSize = GridSize(10, 8)
    override def getCurrentOffset = offset
    override val graphicsSize: Size = Sizes.create(3, 5)
  })
  "Out of bounds returns None" in {
    withOffset(MapPoint(0, 0))(1, Left) shouldReturn None
    withOffset(MapPoint(0, 0))(1, Up) shouldReturn None
    withOffset(MapPoint(0, 3))(1, Down) shouldReturn None
    withOffset(MapPoint(7, 0))(1, Right) shouldReturn None
  }
  "n=1 Goes in direction" in {
    val $ = withOffset(MapPoint(1, 2))
    $(1, Left) shouldReturn Some(MapPoint(0, 2))
    $(1, Up) shouldReturn Some(MapPoint(1, 1))
    $(1, Down) shouldReturn Some(MapPoint(1, 3))
    $(1, Right) shouldReturn Some(MapPoint(2, 2))
  }
  "n=k goes to k if possible" in {
    val $ = new Scroller(new ScrollableMapViewProperties {
      override def getCurrentMapSize = GridSize(100, 80)
      override def getCurrentOffset = MapPoint(10, 20)
      override val graphicsSize: Size = Sizes.create(3, 5)
    })
    $(4, Left) shouldReturn Some(MapPoint(6, 20))
    $(4, Up) shouldReturn Some(MapPoint(10, 16))
    $(4, Down) shouldReturn Some(MapPoint(10, 24))
    $(4, Right) shouldReturn Some(MapPoint(14, 20))
  }
  "Goes to maximum n if k is too large possible" in {
    val $ = withOffset(MapPoint(1, 2))
    $(8, Left) shouldReturn Some(MapPoint(0, 2))
    $(8, Up) shouldReturn Some(MapPoint(1, 0))
    $(8, Down) shouldReturn Some(MapPoint(1, 3))
    $(8, Right) shouldReturn Some(MapPoint(7, 2))
  }

  "center" - {
    val $ = new Scroller(new ScrollableMapViewProperties {
      override def getCurrentMapSize = GridSize(100, 80)
      override def getCurrentOffset = MapPoint(3, 1)
      override val graphicsSize: Size = Sizes.create(5, 3)
    })
    def center(x: Int, y: Int) = $.center(MapPoint(x, y), MapPoint(5, 2))
    "Can center" in {
      center(8, 1) shouldReturn MapPoint(6, 0)
    }
    "Can't center" - {
      "corners" in {
        center(0, 0) shouldReturn MapPoint(0, 0)
        center(99, 0) shouldReturn MapPoint(95, 0)
        center(0, 79) shouldReturn MapPoint(0, 77)
        center(99, 79) shouldReturn MapPoint(95, 77)
      }
      "overlaps" in {
        center(8, 0) shouldReturn MapPoint(6, 0)
        center(1, 5) shouldReturn MapPoint(0, 4)
        center(98, 5) shouldReturn MapPoint(95, 4)
        center(4, 78) shouldReturn MapPoint(2, 77)
      }
    }
  }
}
