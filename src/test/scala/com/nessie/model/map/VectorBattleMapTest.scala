package com.nessie.model.map

class VectorBattleMapTest extends BattleMapTest {
  protected override def createBattleMap(width: Int, height: Int) =
    VectorBattleMap(width = width, height = height)
}
