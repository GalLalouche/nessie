package com.nessie.model.map

// TODO modify test
class DictMapTest extends BattleMapTest {
  protected override def createBattleMap(width: Int, height: Int) =
    BattleMap.create(DictGrid, width = width, height = height)
}
