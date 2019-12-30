package com.nessie.view.zirconview.map

import com.nessie.model.map.{GridSize, MapPoint}
import org.hexworks.zircon.api.{Positions, Sizes}
import org.scalatest.FreeSpec

import common.test.AuxSpecs

//noinspection NameBooleanParameters
class MapPointConverterImplTest extends FreeSpec with AuxSpecs {
  private val $ = new MapPointConverterImpl(
    new ScrollableMapViewProperties {
      override def getCurrentMapSize = GridSize(100, 200)
      override def getCurrentOffset = MapPoint(3, 5)
      override val graphicsSize = Sizes.create(20, 30)
    },
    Positions.create(2, 8),
  )
  "toAbsolutePosition" in {
    def checkNone(x: Int, y: Int): Unit = $.toAbsolutePosition(MapPoint(x, y)) shouldReturn None
    def checkSome(mpx: Int, mpy: Int, px: Int, py: Int): Unit =
      $.toAbsolutePosition(MapPoint(mpx, mpy)).get shouldReturn Positions.create(px, py)
    checkNone(0, 0)
    checkNone(-10, 0)
    checkNone(0, -10)
    checkNone(100, 0)
    checkNone(0, 200)
    checkSome(3, 5, 2, 8)
    checkSome(8, 15, 7, 18)
    checkSome(13, 15, 12, 18)
    checkNone(23, 25)
    checkNone(22, 35)
    checkSome(22, 34, 21, 37)
    checkNone(2, 5)
    checkNone(3, 4)
  }
  "toRelativePosition" in {
    def checkNone(x: Int, y: Int): Unit = $.toRelativePosition(MapPoint(x, y)) shouldReturn None
    def checkSome(mpx: Int, mpy: Int, px: Int, py: Int): Unit =
      $.toRelativePosition(MapPoint(mpx, mpy)).get shouldReturn Positions.create(px, py)
    checkNone(0, 0)
    checkNone(-10, 0)
    checkNone(0, -10)
    checkNone(100, 0)
    checkNone(0, 200)
    checkSome(3, 5, 0, 0)
    checkSome(8, 15, 5, 10)
    checkSome(13, 15, 10, 10)
    checkNone(23, 25)
    checkNone(22, 35)
    checkSome(22, 34, 19, 29)
    checkNone(2, 5)
    checkNone(3, 4)
  }
  "fromAbsolutePosition" in {
    def checkNone(x: Int, y: Int): Unit = $.fromAbsolutePosition(Positions.create(x, y)) shouldReturn None
    def checkSome(px: Int, py: Int, mpx: Int, mpy: Int): Unit =
      $.fromAbsolutePosition(Positions.create(px, py)) shouldReturn Some(MapPoint(mpx, mpy))
    checkNone(0, 0)
    checkNone(-10, 0)
    checkNone(0, -10)
    checkSome(2, 8, 3, 5)
    checkNone(1, 8)
    checkNone(2, 7)
    checkSome(10, 15, 11, 12)
    checkSome(20, 25, 21, 22)
    checkSome(21, 37, 22, 34)
    checkNone(22, 37)
    checkNone(21, 38)
  }
  "fromRelativePosition" in {
    def checkThrows(x: Int, y: Int): Unit =
      an[IndexOutOfBoundsException] shouldBe thrownBy {$.fromRelativePosition(Positions.create(x, y))}
    def checkSome(px: Int, py: Int, mpx: Int, mpy: Int): Unit =
      $.fromRelativePosition(Positions.create(px, py)) shouldReturn MapPoint(mpx, mpy)
    checkThrows(-2, -8)
    checkThrows(-12, -8)
    checkThrows(-2, -18)
    checkSome(0, 0, 3, 5)
    checkThrows(-1, 0)
    checkThrows(0, -1)
    checkSome(8, 7, 11, 12)
    checkSome(18, 17, 21, 22)
    checkSome(19, 29, 22, 34)
    checkThrows(20, 29)
    checkThrows(19, 30)
  }

  "isInBounds" in {
    def check(x: Int, y: Int, expectedResult: Boolean): Unit =
      $.isInBounds(MapPoint(x, y)) shouldReturn expectedResult
    check(0, 0, false)
    check(-10, 0, false)
    check(0, -10, false)
    check(100, 0, false)
    check(0, 200, false)
    check(3, 5, true)
    check(8, 15, true)
    check(13, 15, true)
    check(23, 25, false)
    check(22, 35, false)
    check(22, 34, true)
    check(2, 5, false)
    check(3, 4, false)
  }
  "isAbsolutePositionInBounds" in {
    def check(x: Int, y: Int, expectedResult: Boolean): Unit =
      $.isAbsolutePositionInBounds(Positions.create(x, y)) shouldReturn expectedResult
    check(0, 0, false)
    check(-10, 0, false)
    check(0, -10, false)
    check(2, 8, true)
    check(1, 8, false)
    check(2, 7, false)
    check(10, 15, true)
    check(20, 25, true)
    check(21, 37, true)
    check(22, 37, false)
    check(21, 38, false)
  }
  "isRelativePositionInBounds" in {
    def check(x: Int, y: Int, expectedResult: Boolean): Unit =
      $.isRelativePositionInBounds(Positions.create(x, y)) shouldReturn expectedResult
    check(-10, 0, false)
    check(0, -10, false)
    check(0, 0, true)
    check(-1, 0, false)
    check(0, -1, false)
    check(8, 7, true)
    check(18, 17, true)
    check(19, 29, true)
    check(20, 29, false)
    check(19, 30, false)
  }
  "center" in {
    $.center shouldReturn MapPoint(13, 20)
    new MapPointConverterImpl(
      new ScrollableMapViewProperties {
        override def getCurrentMapSize = GridSize(100, 200)
        override def getCurrentOffset = MapPoint(0, 0)
        override val graphicsSize = Sizes.create(9, 5)
      },
      Positions.create(2, 8),
    ).center shouldReturn MapPoint(4, 2)
  }
}
