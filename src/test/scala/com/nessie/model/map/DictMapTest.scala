package com.nessie.model.map

// TODO modify test
class DictMapTest extends BattleMapTest {
  protected override def createBattleMap(gs: GridSize) = BattleMap.create(DictGrid, gs)
}
