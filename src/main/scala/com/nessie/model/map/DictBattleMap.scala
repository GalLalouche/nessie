package com.nessie.model.map

class DictBattleMap private(
    objects: Map[MapPoint, BattleMapObject], width: Int, height: Int
) extends BattleMap(width, height) {
  override def apply(p: MapPoint) = objects.getOrElse(p, EmptyMapObject)

  override def internalPlace(p: MapPoint, o: BattleMapObject) = new DictBattleMap(
    if (o == EmptyMapObject) objects - p else objects + (p -> o), width, height)
}

object DictBattleMap {
  def apply(width: Int, height: Int) = new DictBattleMap(Map(), width = width, height = height)
  def parser: BattleMapParser = BattleMapParser.fromPoints(apply)
}
