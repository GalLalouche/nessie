package com.nessie.model.map

class DictMapTest extends BattleMapTest {
  protected override def createBattleMap(width: Int, height: Int) =
    VectorBattleMap(width = width, height = height)
}
