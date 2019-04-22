package com.nessie.model.map

import com.nessie.model.map.Direction._
import common.AuxSpecs
import org.scalatest.FreeSpec

class DirectionalMapPointTest extends FreeSpec with AuxSpecs {
  "Canonical direction" - {
    "Down" in {
      val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Down)
      x shouldReturn 5
      y shouldReturn 5
      d shouldReturn Down
    }
    "Right" in {
      val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Right)
      x shouldReturn 5
      y shouldReturn 5
      d shouldReturn Right
    }
    "Up" - {
      "When y == 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 0, Up)
        x shouldReturn 5
        y shouldReturn 0
        d shouldReturn Up
      }
      "When y != 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Up)
        x shouldReturn 5
        y shouldReturn 4
        d shouldReturn Down
      }
    }
    "Left" - {
      "When x == 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(0, 5, Left)
        x shouldReturn 0
        y shouldReturn 5
        d shouldReturn Left
      }
      "When y != 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Left)
        x shouldReturn 4
        y shouldReturn 5
        d shouldReturn Right
      }
    }
  }

  "points" in {
    DirectionalMapPoint(2, 5, Up).points shouldReturn(MapPoint(2, 4), MapPoint(2, 5))
    DirectionalMapPoint(2, 5, Down).points shouldReturn(MapPoint(2, 5), MapPoint(2, 6))
    DirectionalMapPoint(2, 5, Left).points shouldReturn(MapPoint(1, 5), MapPoint(2, 5))
    DirectionalMapPoint(2, 5, Right).points shouldReturn(MapPoint(2, 5), MapPoint(3, 5))
  }

  "between" in {
    DirectionalMapPoint.between(MapPoint(2, 5), MapPoint(1, 5)) shouldReturn DirectionalMapPoint(1, 5, Right)
    DirectionalMapPoint.between(MapPoint(2, 5), MapPoint(3, 5)) shouldReturn DirectionalMapPoint(3, 5, Left)
    DirectionalMapPoint.between(MapPoint(2, 5), MapPoint(2, 4)) shouldReturn DirectionalMapPoint(2, 4, Down)
    DirectionalMapPoint.between(MapPoint(2, 5), MapPoint(2, 6)) shouldReturn DirectionalMapPoint(2, 6, Up)
  }
}
