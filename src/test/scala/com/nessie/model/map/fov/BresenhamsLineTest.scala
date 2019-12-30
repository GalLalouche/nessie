package com.nessie.model.map.fov

import com.nessie.model.map.MapPoint
import org.scalatest.FreeSpec

import common.test.AuxSpecs

class BresenhamsLineTest extends FreeSpec with AuxSpecs {
  // Shorter, for alignment
  private def mp(x: Int, y: Int) = MapPoint(x = x, y = y)
  "thick" - {
    "Some test" in {
      BresenhamsLine.thick(MapPoint(0, 2), MapPoint(4, 0)) shouldMultiSetEqual Vector(
        /*               */ mp(2, 0), mp(3, 0), mp(4, 0),
        mp(0, 1), mp(1, 1), mp(2, 1), mp(3, 1), mp(4, 1),
        mp(0, 2), mp(1, 2), mp(2, 2), /*               */
      )
    }
  }
}
