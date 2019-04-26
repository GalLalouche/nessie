package com.nessie.model.map

class VectorSimpleBattleMapTest extends SimpleBattleMapTest {
  protected override def createBattleMap(width: Int, height: Int) =
    VectorSimpleBattleMap(width = width, height = height)
}
