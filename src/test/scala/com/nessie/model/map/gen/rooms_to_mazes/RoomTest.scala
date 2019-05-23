package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.model.map.MapPoint
import common.AuxSpecs
import org.scalatest.FreeSpec
import org.scalatest.Inspectors._

class RoomTest extends FreeSpec with AuxSpecs {
  //  01234
  // 0_____
  // 1_____
  // 2_xxx_
  // 3_xxx_
  // 4_xxx_
  // 5_xxx_
  // 6_____
  // 7_____
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
    $.mapPoints shouldMultiSetEqual Vector(
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

  "distanceTo" - {
    "same room returns 0" in {
      forAll($.mapPoints) {$ distanceTo _ shouldReturn 0}
    }
    "adjacent returns 1" in {
      $ distanceTo MapPoint(2, 1) shouldReturn 1
      $ distanceTo MapPoint(0, 4) shouldReturn 1
      $ distanceTo MapPoint(4, 3) shouldReturn 1
      $ distanceTo MapPoint(2, 6) shouldReturn 1
    }
    "non-adjacent" in {
      $ distanceTo MapPoint(2, 0) shouldReturn 2
      $ distanceTo MapPoint(0, 1) shouldReturn 2
      $ distanceTo MapPoint(0, 7) shouldReturn 3
      $ distanceTo MapPoint(6, 4) shouldReturn 3
      $ distanceTo MapPoint(7, 6) shouldReturn 5
    }
  }
}
