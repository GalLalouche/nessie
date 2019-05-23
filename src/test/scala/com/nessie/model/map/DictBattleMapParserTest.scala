package com.nessie.model.map

class DictBattleMapParserTest extends BattleMapParserTest {
  protected override def createParser: BattleMapParser = DictBattleMap.parser
}
