package com.nessie.model.units.abilities
import com.nessie.model.map.{BattleMap, MapPoint}

trait MeleeAbility extends UnitAbility {
  addConstraint(new CanBeUsed {
    override def apply(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean =
      source.manhattanDistanceTo(destination) == 1
  })
}
