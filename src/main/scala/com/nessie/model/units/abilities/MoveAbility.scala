package com.nessie.model.units.abilities

import com.nessie.gm.GameState
import com.nessie.model.map.{BattleMap, MapPoint}

case class MoveAbility(range: Int) extends UnitAbility {
  addConstraint(new CanBeUsed {
    override def apply(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean =
      battleMap.isEmptyAt(destination) && source.manhattanDistanceTo(destination) <= range
  })
  override def applyTo(source: MapPoint, destination: MapPoint) =
    GameState.map.modify(_.move(source).to(destination))
}
