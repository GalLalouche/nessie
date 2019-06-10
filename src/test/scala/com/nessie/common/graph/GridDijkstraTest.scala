package com.nessie.common.graph

import com.nessie.common.graph.GridDijkstra.Blockable
import com.nessie.model.map.{Grid, GridParser, MapPoint, VectorGrid}
import com.nessie.model.map.GridParser.CharParsable
import common.AuxSpecs
import org.scalatest.FreeSpec

class GridDijkstraTest extends FreeSpec with AuxSpecs {
  private implicit val BooleanBlocks: Blockable[Boolean] = Blockable(identity)
  private implicit val BooleanCharParsable: CharParsable[Boolean] = CharParsable {
    case 'f' | 'F' => false
    case 't' | 'T' => true
  }
  private def parse(s: String): Grid[Boolean] = GridParser.fromFactory(VectorGrid).parse(s)
  private def round(map: Map[MapPoint, Double]): Map[MapPoint, Int] = map.mapValues(_.*(100).round.toInt)
  private def round(o: Option[Double]): Int = o.get.*(100).round.toInt
  // Shorter for alignment: x, y, roundedDistance
  type ShortHand = (Int, Int, Int)
  def convert(sh: ShortHand*): Map[MapPoint, Int] = sh.map(e => MapPoint(e._1, e._2) -> e._3).toMap
  "all" - {
    "horizontal" in {
      val grid = parse("ffff")
      GridDijkstra(grid, MapPoint(1, 0), 1) shouldReturn Map(MapPoint(0, 0) -> 1, MapPoint(2, 0) -> 1)
    }
    "vertical" in {
      val grid = parse(
        """|f
           |f
           |f
           |f""".stripMargin)
      GridDijkstra(grid, MapPoint(0, 1), 1) shouldReturn Map(MapPoint(0, 2) -> 1, MapPoint(0, 0) -> 1)
    }
    "horizontal blocked" in {
      val grid = parse("ftf")
      GridDijkstra(grid, MapPoint(0, 0), 2) shouldReturn Map()
    }
    "vertical blocked" in {
      val grid = parse("f\nt\nf")
      GridDijkstra(grid, MapPoint(0, 0), 2) shouldReturn Map()
    }
    "diagonal" in {
      val grid = parse(
        """|ff
           |ff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), 3)) shouldMultiSetEqual convert(
        /* source */ (1, 0, 100),
        (0, 1, 100), (1, 1, 141)
      )
    }
    "Can't take diagonal around a blocker" in {
      val grid = parse(
        """|ft
           |ff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), 3)) shouldMultiSetEqual convert(
        /* source       block  */
        (0, 1, 100), (1, 1, 200)
      )
    }
    "blocker in the middle 1" in {
      val grid = parse(
        """|fff
           |ftf
           |fff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), 3)) shouldMultiSetEqual convert(
        /* source */ (1, 0, 100), (2, 0, 200),
        (0, 1, 100), /* block  */ (2, 1, 300),
        (0, 2, 200), (1, 2, 300), /* too far */
      )
    }
    "blocker in the middle 2" in {
      val grid = parse(
        """|fffff
           |fftff
           |fffff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), 6)) shouldMultiSetEqual convert(
        /* source */ (1, 0, 100), (2, 0, 200), (3, 0, 300), (4, 0, 400),
        (0, 1, 100), (1, 1, 141), /* block  */ (3, 1, 400), (4, 1, 441),
        (0, 2, 200), (1, 2, 224), (2, 2, 324), (3, 2, 424), (4, 2, 524),
      )
    }
    "blockers in the middle 3" in {
      val grid = parse(
        """|fffft
           |fftff
           |tftff
           |fffff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), 8)) shouldMultiSetEqual convert(
        /* source */ (1, 0, 100), (2, 0, 200), (3, 0, 300), /* block  */
        (0, 1, 100), (1, 1, 141), /* block  */ (3, 1, 400), (4, 1, 500),
        /* block  */ (1, 2, 241), /* block  */ (3, 2, 500), (4, 2, 541),
        (0, 3, 441), (1, 3, 341), (2, 3, 441), (3, 3, 541), (4, 3, 624),
      )
    }
    "round about distance" - {
      val grid = parse(
        """|ftf
           |ftf
           |fff""".stripMargin)
      "distance too short" in {
        round(GridDijkstra(grid, MapPoint(0, 0), 5)).contains(MapPoint(2, 0)) shouldReturn false
      }
      "distance large enough" in {
        round(GridDijkstra(grid, MapPoint(0, 0), 6)).apply(MapPoint(2, 0)) shouldReturn 600
      }
    }
  }
  "pairs" - {
    "horizontal" in {
      val grid = parse("ffff")
      GridDijkstra(grid, MapPoint(1, 0), MapPoint(3, 0)) shouldReturn Some(2)
    }
    "vertical" in {
      val grid = parse(
        """|f
           |f
           |f
           |f""".stripMargin)
      GridDijkstra(grid, MapPoint(0, 1), MapPoint(0, 3)) shouldReturn Some(2)
    }
    "horizontal blocked" in {
      val grid = parse("ftf")
      GridDijkstra(grid, MapPoint(0, 0), MapPoint(2, 0)) shouldReturn None
    }
    "vertical blocked" in {
      val grid = parse("f\nt\nf")
      GridDijkstra(grid, MapPoint(0, 0), MapPoint(0, 2)) shouldReturn None
    }
    "diagonal" in {
      val grid = parse(
        """|ff
           |ff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), MapPoint(1, 1))) shouldReturn 141
    }
    "Can't take diagonal around a blocker" in {
      val grid = parse(
        """|ft
           |ff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), MapPoint(1, 1))) shouldReturn 200
    }
    "blocker in the middle 1" in {
      val grid = parse(
        """|fff
           |ftf
           |fff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), MapPoint(2, 2))) shouldReturn 400
    }
    "blocker in the middle 2" in {
      val grid = parse(
        """|fffff
           |fftff
           |fffff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), MapPoint(3, 2))) shouldReturn 424
    }
    "blockers in the middle 3" in {
      val grid = parse(
        """|fffft
           |fftff
           |tftff
           |fffff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), MapPoint(4, 2))) shouldReturn 541
    }

    "round about distance" in {
      val grid = parse(
        """|ftf
           |ftf
           |fff""".stripMargin)
      round(GridDijkstra(grid, MapPoint(0, 0), MapPoint(2, 0))) shouldReturn 600
    }
  }
}
