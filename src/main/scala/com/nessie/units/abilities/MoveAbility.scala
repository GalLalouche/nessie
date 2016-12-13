package com.nessie.units.abilities

import com.nessie.gm.GameState
import com.nessie.map.model.{BattleMap, MapPoint}

class MoveAbility extends UnitAbility {
  override def applyTo(source: MapPoint, destination: MapPoint) =
    GameState.map.modify(_.move(source).to(destination))
}

private object MoveAbility extends CanBeUsed {
  override def apply(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean =
    battleMap.isOccupiedAt(source) && !battleMap.isOccupiedAt(destination)
}
