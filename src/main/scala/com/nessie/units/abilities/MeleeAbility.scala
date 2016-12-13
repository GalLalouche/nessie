package com.nessie.units.abilities
import com.nessie.map.model.{BattleMap, MapPoint}

trait MeleeAbility extends UnitAbility {
  addConstraint(new CanBeUsed {
    override def apply(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean =
      source.manhattanDistanceTo(destination) == 1
  })
}
