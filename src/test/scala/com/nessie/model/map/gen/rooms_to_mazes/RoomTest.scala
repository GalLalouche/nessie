package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.model.map.MapPoint
import common.AuxSpecs
import org.scalatest.FreeSpec

class RoomTest extends FreeSpec with AuxSpecs {
  // _____
  // _____
  // _xxx_
  // _xxx_
  // _xxx_
  // _xxx_
  // _____
  private val $ = Room(x = 1, y = 2, w = 3, h = 4)
  "pointNotInRectangle" in {
    $.pointInRectangle(MapPoint(1, 2)) shouldReturn true
    $.pointInRectangle(MapPoint(1, 3)) shouldReturn true
    $.pointInRectangle(MapPoint(1, 4)) shouldReturn true
    $.pointInRectangle(MapPoint(1, 5)) shouldReturn true

    $.pointInRectangle(MapPoint(2, 2)) shouldReturn true
    $.pointInRectangle(MapPoint(2, 3)) shouldReturn true
    $.pointInRectangle(MapPoint(2, 4)) shouldReturn true
    $.pointInRectangle(MapPoint(2, 5)) shouldReturn true

    $.pointInRectangle(MapPoint(3, 2)) shouldReturn true
    $.pointInRectangle(MapPoint(3, 3)) shouldReturn true
    $.pointInRectangle(MapPoint(3, 4)) shouldReturn true
    $.pointInRectangle(MapPoint(3, 5)) shouldReturn true

    $.pointInRectangle(MapPoint(0, 1)) shouldReturn false
    $.pointInRectangle(MapPoint(1, 1)) shouldReturn false
    $.pointInRectangle(MapPoint(2, 1)) shouldReturn false
    $.pointInRectangle(MapPoint(3, 1)) shouldReturn false
    $.pointInRectangle(MapPoint(4, 1)) shouldReturn false

    $.pointInRectangle(MapPoint(0, 2)) shouldReturn false
    $.pointInRectangle(MapPoint(4, 2)) shouldReturn false

    $.pointInRectangle(MapPoint(0, 3)) shouldReturn false
    $.pointInRectangle(MapPoint(4, 3)) shouldReturn false

    $.pointInRectangle(MapPoint(0, 6)) shouldReturn false
    $.pointInRectangle(MapPoint(1, 6)) shouldReturn false
    $.pointInRectangle(MapPoint(2, 6)) shouldReturn false
    $.pointInRectangle(MapPoint(3, 6)) shouldReturn false
  }

  "isOverlapping" - {
    "overlap" in {
      $.isOverlapping(Room(3, 2, 4, 5)) shouldReturn true
    }
    "cross" in {
      Room(0, 5, 10, 1).isOverlapping(Room(5, 0, 1, 10)) shouldReturn true
    }
    "disjoint" in {
      $.isOverlapping(Room(4, 3, 10, 10)) shouldReturn false
    }
  }

  "isAdjacent" - {
    "right" in {
      $.isAdjacent(Room(x = 4, y = 3, w = 3, h = 2)) shouldReturn true
    }
    "left" in {
      $.isAdjacent(Room(x = 0, y = 0, w = 1, h = 5)) shouldReturn true
    }
    "up" in {
      $.isAdjacent(Room(x = 0, y = 0, w = 5, h = 2)) shouldReturn true
    }
    "down" in {
      $.isAdjacent(Room(x = 0, y = 6, w = 5, h = 1)) shouldReturn true
    }
    "diagonal" in {
      $.isAdjacent(Room(x = 4, y = 6, w = 1, h = 1)) shouldReturn true
    }
    "false" in {
      $.isAdjacent(Room(x = 5, y = 6, w = 5, h = 5)) shouldReturn false
    }
  }

  "mapPoints" in {
    $.mapPoints.size shouldReturn 12
    $.mapPoints shouldSetEqual Vector(
      MapPoint(1, 2),
      MapPoint(1, 3),
      MapPoint(1, 4),
      MapPoint(1, 5),
      MapPoint(2, 2),
      MapPoint(2, 3),
      MapPoint(2, 4),
      MapPoint(2, 5),
      MapPoint(3, 2),
      MapPoint(3, 3),
      MapPoint(3, 4),
      MapPoint(3, 5),
    )
  }
}
