package com.nessie.model.map

class VectorMapTest extends BattleMapTest {
  protected override def createBattleMap(width: Int, height: Int) =
    VectorBattleMap.apply(width = width, height = height)
}
