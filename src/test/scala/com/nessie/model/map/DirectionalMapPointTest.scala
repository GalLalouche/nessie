package com.nessie.model.map

import common.AuxSpecs
import org.scalatest.FreeSpec
import common.rich.RichT._

class DirectionalMapPointTest extends FreeSpec with AuxSpecs {
  "Canonical direction" - {
    "Down" in {
      val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Direction.DOWN)
      x shouldReturn 5
      y shouldReturn 5
      d shouldReturn Direction.DOWN
    }
    "Right" in {
      val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Direction.RIGHT)
      x shouldReturn 5
      y shouldReturn 5
      d shouldReturn Direction.RIGHT
    }
    "Up" - {
      "When y == 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 0, Direction.UP)
        x shouldReturn 5
        y shouldReturn 0
        d shouldReturn Direction.UP
      }
      "When y != 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Direction.UP)
        x shouldReturn 5
        y shouldReturn 4
        d shouldReturn Direction.DOWN
      }
    }
    "Left" - {
      "When x == 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(0, 5, Direction.LEFT)
        x shouldReturn 0
        y shouldReturn 5
        d shouldReturn Direction.LEFT
      }
      "When y != 0" in {
        val DirectionalMapPoint(x, y, d) = DirectionalMapPoint(5, 5, Direction.LEFT)
        x shouldReturn 4
        y shouldReturn 5
        d shouldReturn Direction.RIGHT
      }
    }
  }
}
