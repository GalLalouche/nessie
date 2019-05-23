package com.nessie.model.map

import common.AuxSpecs
import org.scalatest.FreeSpec

class DirectionTest extends FreeSpec with AuxSpecs {
  "between" - {
    "Up" in {
      Direction.from(MapPoint(0, 1), MapPoint(0, 0)).get shouldReturn Direction.Up
    }
    "Down" in {
      Direction.from(MapPoint(0, 0), MapPoint(0, 1)).get shouldReturn Direction.Down
    }
    "Left" in {
      Direction.from(MapPoint(1, 0), MapPoint(0, 0)).get shouldReturn Direction.Left
    }
    "Right" in {
      Direction.from(MapPoint(0, 0), MapPoint(1, 0)).get shouldReturn Direction.Right
    }
  }
}
