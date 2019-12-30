package com.nessie.model.map.gen.cellular_automata

import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.model.map.Direction.{Down => D, Left => L, Right => R, Up => U}
import com.nessie.model.map.gen.cellular_automata.PathWidener.Width
import org.scalatest.FreeSpec

import common.test.AuxSpecs

class PathWidenerTest extends FreeSpec with AuxSpecs {
  private def fromDirections(ds: Seq[Direction]): Seq[MapPoint] =
    ds.foldLeft(Vector(MapPoint(5, 5)))((v, d) => v :+ v.last.go(d))
  private def widen(width: Width)(ds: Seq[Direction]): Iterable[MapPoint] =
    PathWidener(fromDirections(ds), width)
  "one returns self" in {
    val path = Vector(R, R, R, R, U, U, R, R, D, D, D, D, D, L, L, L, L)
    widen(Width.One)(path) shouldReturn fromDirections(path)
  }

  "TwoRightUp" - {
    val go = widen(Width.TwoRightUp) _
    "simple" - {
      "Right only" in {
        go(Vector(R, R)) shouldMultiSetEqual Vector(
          MapPoint(5, 4), MapPoint(6, 4), MapPoint(7, 4),
          MapPoint(5, 5), MapPoint(6, 5), MapPoint(7, 5),
        )
      }
      "Left only" in {
        go(Vector(L, L)) shouldMultiSetEqual Vector(
          MapPoint(3, 5), MapPoint(4, 5), MapPoint(5, 5),
          MapPoint(3, 6), MapPoint(4, 6), MapPoint(5, 6),
        )
      }
      "Up only" in {
        go(Vector(U, U)) shouldMultiSetEqual Vector(
          MapPoint(4, 3), MapPoint(5, 3),
          MapPoint(4, 4), MapPoint(5, 4),
          MapPoint(4, 5), MapPoint(5, 5),
        )
      }
      "Down only" in {
        go(Vector(D, D)) shouldMultiSetEqual Vector(
          MapPoint(5, 5), MapPoint(6, 5),
          MapPoint(5, 6), MapPoint(6, 6),
          MapPoint(5, 7), MapPoint(6, 7),
        )
      }
      "Corners" - {
        "→↑" in {
          go(Vector(R, U)) shouldMultiSetEqual Vector(
            MapPoint(5, 4), MapPoint(6, 4),
            MapPoint(5, 5), MapPoint(6, 5),
          )
        }
        "→↓" in {
          go(Vector(R, D)) shouldMultiSetEqual Vector(
            MapPoint(5, 4), MapPoint(6, 4), MapPoint(7, 4),
            MapPoint(5, 5), MapPoint(6, 5), MapPoint(7, 5),
            /*           */ MapPoint(6, 6), MapPoint(7, 6),
          )
        }
        "↑→" in {
          go(Vector(U, R)) shouldMultiSetEqual Vector(
            MapPoint(4, 3), MapPoint(5, 3), MapPoint(6, 3),
            MapPoint(4, 4), MapPoint(5, 4), MapPoint(6, 4),
            MapPoint(4, 5), MapPoint(5, 5),
          )
        }
        "↓→" in {
          go(Vector(D, R)) shouldMultiSetEqual Vector(
            MapPoint(5, 5), MapPoint(6, 5),
            MapPoint(5, 6), MapPoint(6, 6),
          )
        }
        "←↑" in {
          go(Vector(L, U)) shouldMultiSetEqual Vector(
            MapPoint(3, 4), MapPoint(4, 4),
            MapPoint(3, 5), MapPoint(4, 5), MapPoint(5, 5),
            MapPoint(3, 6), MapPoint(4, 6), MapPoint(5, 6),
          )
        }
        "←↓" in {
          go(Vector(L, D)) shouldMultiSetEqual Vector(
            MapPoint(4, 5), MapPoint(5, 5),
            MapPoint(4, 6), MapPoint(5, 6),
          )
        }
        "↑←" in {
          go(Vector(U, L)) shouldMultiSetEqual Vector(
            MapPoint(4, 4), MapPoint(5, 4),
            MapPoint(4, 5), MapPoint(5, 5),
          )
        }
        "↓←" in {
          go(Vector(D, L)) shouldMultiSetEqual Vector(
            /*           */ MapPoint(5, 5), MapPoint(6, 5),
            MapPoint(4, 6), MapPoint(5, 6), MapPoint(6, 6),
            MapPoint(4, 7), MapPoint(5, 7), MapPoint(6, 7),
          )
        }
      }
    }
  }

  "TwoLeftDown" - {
    val go = widen(Width.TwoLeftDown) _
    "simple" - {
      "Right only" in {
        go(Vector(R, R)) shouldMultiSetEqual Vector(
          MapPoint(5, 5), MapPoint(6, 5), MapPoint(7, 5),
          MapPoint(5, 6), MapPoint(6, 6), MapPoint(7, 6),
        )
      }
      "Left only" in {
        go(Vector(L, L)) shouldMultiSetEqual Vector(
          MapPoint(3, 4), MapPoint(4, 4), MapPoint(5, 4),
          MapPoint(3, 5), MapPoint(4, 5), MapPoint(5, 5),
        )
      }
      "Up only" in {
        go(Vector(U, U)) shouldMultiSetEqual Vector(
          MapPoint(5, 3), MapPoint(6, 3),
          MapPoint(5, 4), MapPoint(6, 4),
          MapPoint(5, 5), MapPoint(6, 5),
        )
      }
      "Down only" in {
        go(Vector(D, D)) shouldMultiSetEqual Vector(
          MapPoint(4, 5), MapPoint(5, 5),
          MapPoint(4, 6), MapPoint(5, 6),
          MapPoint(4, 7), MapPoint(5, 7),
        )
      }
      "Corners" - {
        "→↑" in {
          go(Vector(R, U)) shouldMultiSetEqual Vector(
            /*           */ MapPoint(6, 4), MapPoint(7, 4),
            MapPoint(5, 5), MapPoint(6, 5), MapPoint(7, 5),
            MapPoint(5, 6), MapPoint(6, 6), MapPoint(7, 6),
          )
        }
        "→↓" in {
          go(Vector(R, D)) shouldMultiSetEqual Vector(
            MapPoint(5, 5), MapPoint(6, 5),
            MapPoint(5, 6), MapPoint(6, 6),
          )
        }
        "↑→" in {
          go(Vector(U, R)) shouldMultiSetEqual Vector(
            MapPoint(5, 4), MapPoint(6, 4),
            MapPoint(5, 5), MapPoint(6, 5),
          )
        }
        "↓→" in {
          go(Vector(D, R)) shouldMultiSetEqual Vector(
            MapPoint(4, 5), MapPoint(5, 5),
            MapPoint(4, 6), MapPoint(5, 6), MapPoint(6, 6),
            MapPoint(4, 7), MapPoint(5, 7), MapPoint(6, 7),
          )
        }
        "←↑" in {
          go(Vector(L, U)) shouldMultiSetEqual Vector(
            MapPoint(4, 4), MapPoint(5, 4),
            MapPoint(4, 5), MapPoint(5, 5),
          )
        }
        "←↓" in {
          go(Vector(L, D)) shouldMultiSetEqual Vector(
            MapPoint(3, 4), MapPoint(4, 4), MapPoint(5, 4),
            MapPoint(3, 5), MapPoint(4, 5), MapPoint(5, 5),
            MapPoint(3, 6), MapPoint(4, 6),
          )
        }
        "↑←" in {
          go(Vector(U, L)) shouldMultiSetEqual Vector(
            MapPoint(4, 3), MapPoint(5, 3), MapPoint(6, 3),
            MapPoint(4, 4), MapPoint(5, 4), MapPoint(6, 4),
            /*           */ MapPoint(5, 5), MapPoint(6, 5),
          )
        }
        "↓←" in {
          go(Vector(D, L)) shouldMultiSetEqual Vector(
            MapPoint(4, 5), MapPoint(5, 5),
            MapPoint(4, 6), MapPoint(5, 6),
          )
        }
      }
    }
  }

  "Three" - {
    val go = widen(Width.Three) _
    "simple" - {
      "Right only" in {
        go(Vector(R, R)) shouldMultiSetEqual Vector(
          MapPoint(5, 4), MapPoint(6, 4), MapPoint(7, 4),
          MapPoint(5, 5), MapPoint(6, 5), MapPoint(7, 5),
          MapPoint(5, 6), MapPoint(6, 6), MapPoint(7, 6),
        )
      }
      "Left only" in {
        go(Vector(L, L)) shouldMultiSetEqual Vector(
          MapPoint(3, 4), MapPoint(4, 4), MapPoint(5, 4),
          MapPoint(3, 5), MapPoint(4, 5), MapPoint(5, 5),
          MapPoint(3, 6), MapPoint(4, 6), MapPoint(5, 6),
        )
      }
      "Up only" in {
        go(Vector(U, U)) shouldMultiSetEqual Vector(
          MapPoint(4, 3), MapPoint(5, 3), MapPoint(6, 3),
          MapPoint(4, 4), MapPoint(5, 4), MapPoint(6, 4),
          MapPoint(4, 5), MapPoint(5, 5), MapPoint(6, 5),
        )
      }
      "Down only" in {
        go(Vector(D, D)) shouldMultiSetEqual Vector(
          MapPoint(4, 5), MapPoint(5, 5), MapPoint(6, 5),
          MapPoint(4, 6), MapPoint(5, 6), MapPoint(6, 6),
          MapPoint(4, 7), MapPoint(5, 7), MapPoint(6, 7),
        )
      }
    }
    "Corner" in {
      go(Vector(R, U)) shouldMultiSetEqual Vector(
        MapPoint(5, 4), MapPoint(5, 5), MapPoint(5, 6),
        MapPoint(6, 4), MapPoint(6, 5), MapPoint(6, 6),
        MapPoint(7, 4), MapPoint(7, 5), MapPoint(7, 6),
      )
    }
  }
}
