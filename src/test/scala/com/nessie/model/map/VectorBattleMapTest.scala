package com.nessie.model.map

// TODO modify test
class VectorBattleMapTest extends BattleMapTest {
  protected override def createBattleMap(width: Int, height: Int) =
    BattleMap.create(VectorGrid, width = width, height = height)
}
