package com.nessie.model.map

import common.AuxSpecs
import org.scalatest.FreeSpec
import common.rich.RichT._

class DirectionalMapPointTest extends FreeSpec with AuxSpecs {
  "Canonical direction" - {
    "Down" in {
      val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Direction.Down)
      x shouldReturn 5
      y shouldReturn 5
      d shouldReturn Direction.Down
    }
    "Right" in {
      val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Direction.Right)
      x shouldReturn 5
      y shouldReturn 5
      d shouldReturn Direction.Right
    }
    "Up" - {
      "When y == 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 0, Direction.Up)
        x shouldReturn 5
        y shouldReturn 0
        d shouldReturn Direction.Up
      }
      "When y != 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Direction.Up)
        x shouldReturn 5
        y shouldReturn 4
        d shouldReturn Direction.Down
      }
    }
    "Left" - {
      "When x == 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(0, 5, Direction.Left)
        x shouldReturn 0
        y shouldReturn 5
        d shouldReturn Direction.Left
      }
      "When y != 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Direction.Left)
        x shouldReturn 4
        y shouldReturn 5
        d shouldReturn Direction.Right
      }
    }
  }
}
