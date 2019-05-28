package com.nessie.model.map

// TODO modify test
class VectorBattleMapTest extends BattleMapTest {
  protected override def createBattleMap(gs: GridSize) = BattleMap.create(VectorGrid, gs)
}
