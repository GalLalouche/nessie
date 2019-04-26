package com.nessie.model.map.gen

import com.nessie.model.map.MapPoint
import common.AuxSpecs
import org.scalatest.FreeSpec

class RoomTest extends FreeSpec with AuxSpecs {
  // _____
  // _xxx_
  // _xxx_
  // _____
  private val $ = Room(x = 1, y = 1, w = 3, h = 2)
  "pointNotInRectangle" in {
    $.pointInRectangle(MapPoint(1, 1)) shouldReturn true
    $.pointInRectangle(MapPoint(1, 2)) shouldReturn true

    $.pointInRectangle(MapPoint(2, 1)) shouldReturn true
    $.pointInRectangle(MapPoint(2, 2)) shouldReturn true

    $.pointInRectangle(MapPoint(3, 1)) shouldReturn true
    $.pointInRectangle(MapPoint(3, 2)) shouldReturn true

    $.pointInRectangle(MapPoint(0, 0)) shouldReturn false
    $.pointInRectangle(MapPoint(1, 0)) shouldReturn false
    $.pointInRectangle(MapPoint(2, 0)) shouldReturn false
    $.pointInRectangle(MapPoint(3, 0)) shouldReturn false
    $.pointInRectangle(MapPoint(4, 0)) shouldReturn false

    $.pointInRectangle(MapPoint(0, 1)) shouldReturn false
    $.pointInRectangle(MapPoint(4, 1)) shouldReturn false

    $.pointInRectangle(MapPoint(0, 2)) shouldReturn false
    $.pointInRectangle(MapPoint(4, 2)) shouldReturn false

    $.pointInRectangle(MapPoint(0, 3)) shouldReturn false
    $.pointInRectangle(MapPoint(1, 3)) shouldReturn false
    $.pointInRectangle(MapPoint(2, 3)) shouldReturn false
    $.pointInRectangle(MapPoint(3, 3)) shouldReturn false
  }

  "isOverlapping" in {
    Room(0, 5, 10, 1).isOverlapping(Room(5, 0, 1, 10)) shouldReturn true
  }
  "isAdjacent" - {
    "right" in {
      $.isAdjacent(Room(x = 1, y = 3, w = 3, h = 2)) shouldReturn true
    }
    "left" in {
      $.isAdjacent(Room(x = 0, y = 0, w = 1, h = 5)) shouldReturn true
    }
    "up" in {
      $.isAdjacent(Room(x = 0, y = 0, w = 5, h = 1)) shouldReturn true
    }
    "down" in {
      $.isAdjacent(Room(x = 3, y = 0, w = 5, h = 1)) shouldReturn true
    }
    "diagonal" in {
      $.isAdjacent(Room(x = 4, y = 3, w = 1, h = 1)) shouldReturn true
    }
    "false" in {
      $.isAdjacent(Room(x = 5, y = 5, w = 5, h = 5)) shouldReturn false
    }
  }
}
