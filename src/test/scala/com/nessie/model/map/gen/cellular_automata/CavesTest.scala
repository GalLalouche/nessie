package com.nessie.model.map.gen.cellular_automata

import com.nessie.model.map.{BattleMapParser, MapPoint, VectorGrid}
import org.scalatest.FreeSpec

import common.test.AuxSpecs

class CavesTest extends FreeSpec with AuxSpecs {
  "from" in {
    val map = BattleMapParser.fromFactory(VectorGrid).parse(
      """_*_*_
        |___*_
        |*****
        |_____""".stripMargin)
    val caves = Caves.from(map)
    caves.caves.map(_.mapPoints.toSet) shouldMultiSetEqual Vector(
      Set(
        MapPoint(0, 0),
        MapPoint(0, 1),
        MapPoint(1, 1),
        MapPoint(2, 1),
        MapPoint(2, 0),
      ),
      Set(
        MapPoint(4, 0),
        MapPoint(4, 1),
      ),
      Set(
        MapPoint(0, 3),
        MapPoint(1, 3),
        MapPoint(2, 3),
        MapPoint(3, 3),
        MapPoint(4, 3),
      ),
    )
  }
}
