package com.nessie.model.map

class DictGridTest extends GridLikeTest(DictGrid) {
  "Regression: deletes default" in {
    val mp = MapPoint(0, 0)
    DictGrid[BattleMapObject](GridSize(1, 1), FullWall).place(mp, EmptyMapObject)(mp) shouldReturn EmptyMapObject
  }
}
