package com.nessie.model.map

import common.AuxSpecs
import org.scalatest.FreeSpec

class MapPointTest extends FreeSpec with AuxSpecs {
  "go" - {
    val p = MapPoint(5, 5)
    "Up" in { p.go(Direction.Up) shouldReturn MapPoint(5, 4) }
    "Down" in { p.go(Direction.Down) shouldReturn MapPoint(5, 6) }
    "Left" in { p.go(Direction.Left) shouldReturn MapPoint(4, 5) }
    "Right" in { p.go(Direction.Right) shouldReturn MapPoint(6, 5) }
  }

  "neighborsAndDiagonals" - {
    "middle" in {
      val p = MapPoint(5, 5)
      val $ = p.neighborsAndDiagonals
      $.size shouldReturn 8
      $ shouldMultiSetEqual Vector(
        4 -> 4,
        4 -> 5,
        4 -> 6,
        5 -> 4,
        5 -> 6,
        6 -> 4,
        6 -> 5,
        6 -> 6,
      ).map(MapPoint.apply)
    }
    "corner shouldn't throw an exception but ignore invalid points" in {
      val p = MapPoint(0, 0)
      val $ = p.neighborsAndDiagonals
      $.size shouldReturn 3
      $ shouldMultiSetEqual Vector(
        0 -> 1,
        1 -> 0,
        1 -> 1,
      ).map(MapPoint.apply)
    }
  }
}
