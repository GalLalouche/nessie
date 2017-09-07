package com.nessie.model.map

class DictBattleMap private(objects: Map[MapPoint, BattleMapObject],
    betweens: Map[DirectionalMapPoint, BetweenMapObject], width: Int, height: Int) extends BattleMap(width, height) {
  override def apply(p: MapPoint) = objects.getOrElse(p, EmptyMapObject)
  override def apply(pd: DirectionalMapPoint) = betweens.getOrElse(pd, EmptyBetweenMapObject)

  override def internalPlace(p: MapPoint, o: BattleMapObject) =
    new DictBattleMap(objects + (p -> o), betweens, width, height)
  override def internalPlace(pd: DirectionalMapPoint, o: BetweenMapObject) =
    new DictBattleMap(objects, betweens + (pd -> o), width, height)
}

object DictBattleMap {
  def apply(width: Int, height: Int) = new DictBattleMap(Map(), Map(), width, height)
}
