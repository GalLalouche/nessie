package com.nessie.model.map

class VectorBattleMapParserTest extends BattleMapParserTest {
  protected override def createParser: BattleMapParser = VectorBattleMap.parser
}
