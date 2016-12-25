package com.nessie.model.map

import common.AuxSpecs
import org.scalatest.FreeSpec

class MapPointTest extends FreeSpec with AuxSpecs {
  "go" - {
    val p = MapPoint(5, 5)
    "Up" in { p.go(Direction.UP) shouldReturn MapPoint(5, 4) }
    "Down" in { p.go(Direction.DOWN) shouldReturn MapPoint(5, 6) }
    "Left" in { p.go(Direction.LEFT) shouldReturn MapPoint(4, 5) }
    "Right" in { p.go(Direction.RIGHT) shouldReturn MapPoint(6, 5) }
  }
}
