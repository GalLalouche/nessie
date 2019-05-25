package com.nessie.model.map

import common.AuxSpecs
import org.scalatest.FreeSpec

class BattleMapParserTest extends FreeSpec with AuxSpecs {
  def createParser: BattleMapParser = BattleMapParser.fromFactory(VectorGrid)

  "empty throws" in {
    an[IllegalArgumentException] shouldBe thrownBy {createParser.parse("")}
  }
  "simple map" in {
    val $ = createParser.parse(
      """|***
         |___
         |***""".stripMargin)
    $.objects shouldReturn Vector(
      MapPoint(0, 0) -> FullWall,
      MapPoint(1, 0) -> FullWall,
      MapPoint(2, 0) -> FullWall,
      MapPoint(0, 1) -> EmptyMapObject,
      MapPoint(1, 1) -> EmptyMapObject,
      MapPoint(2, 1) -> EmptyMapObject,
      MapPoint(0, 2) -> FullWall,
      MapPoint(1, 2) -> FullWall,
      MapPoint(2, 2) -> FullWall,
    )
  }
}
