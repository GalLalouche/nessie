package com.nessie.model.map.gen

import com.nessie.model.map.{Direction, DirectionalMapPoint, MapPoint}
import com.nessie.model.map.Direction._
import common.AuxSpecs
import org.scalatest.FreeSpec

class RoomTest extends FreeSpec with AuxSpecs {
  // _____
  // _xxx_
  // _xxx_
  // _____
  private val $ = Room(x = 1, y = 1, w = 3, h = 2)
  "pointNotInRectangle" in {
    $.pointNotInRectangle(MapPoint(1, 1)) shouldReturn false
    $.pointNotInRectangle(MapPoint(1, 2)) shouldReturn false

    $.pointNotInRectangle(MapPoint(2, 1)) shouldReturn false
    $.pointNotInRectangle(MapPoint(2, 2)) shouldReturn false

    $.pointNotInRectangle(MapPoint(3, 1)) shouldReturn false
    $.pointNotInRectangle(MapPoint(3, 2)) shouldReturn false

    $.pointNotInRectangle(MapPoint(0, 0)) shouldReturn true
    $.pointNotInRectangle(MapPoint(1, 0)) shouldReturn true
    $.pointNotInRectangle(MapPoint(2, 0)) shouldReturn true
    $.pointNotInRectangle(MapPoint(3, 0)) shouldReturn true
    $.pointNotInRectangle(MapPoint(4, 0)) shouldReturn true

    $.pointNotInRectangle(MapPoint(0, 1)) shouldReturn true
    $.pointNotInRectangle(MapPoint(4, 1)) shouldReturn true

    $.pointNotInRectangle(MapPoint(0, 2)) shouldReturn true
    $.pointNotInRectangle(MapPoint(4, 2)) shouldReturn true

    $.pointNotInRectangle(MapPoint(0, 3)) shouldReturn true
    $.pointNotInRectangle(MapPoint(1, 3)) shouldReturn true
    $.pointNotInRectangle(MapPoint(2, 3)) shouldReturn true
    $.pointNotInRectangle(MapPoint(3, 3)) shouldReturn true
    $.pointNotInRectangle(MapPoint(4, 3)) shouldReturn true
  }

  "walls" in {
    def check(p: MapPoint, ds: Direction*) = $.walls(p) shouldSetEqual ds.map(DirectionalMapPoint(p, _))
    check(MapPoint(1, 1), Up, Left)
    check(MapPoint(1, 2), Down, Left)

    check(MapPoint(2, 1), Up)
    check(MapPoint(2, 2), Down)

    check(MapPoint(3, 1), Up, Right)
    check(MapPoint(3, 2), Down, Right)

    check(MapPoint(0, 0))
    check(MapPoint(1, 0))
    check(MapPoint(2, 0))
    check(MapPoint(3, 0))
    check(MapPoint(4, 0))

    check(MapPoint(0, 1))
    check(MapPoint(4, 1))

    check(MapPoint(0, 2))
    check(MapPoint(4, 2))

    check(MapPoint(0, 3))
    check(MapPoint(1, 3))
    check(MapPoint(2, 3))
    check(MapPoint(3, 3))
    check(MapPoint(4, 3))
  }
}
