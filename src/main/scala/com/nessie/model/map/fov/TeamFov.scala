package com.nessie.model.map.fov

import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.{CombatUnit, Owner}

object TeamFov {
  def visibleForOwner(c: CombatUnitObject, map: BattleMap): Set[MapPoint] = visibleForOwner(c.unit, map)
  def visibleForOwner(u: CombatUnit, map: BattleMap): Set[MapPoint] = visibleForOwner(u.owner, map)
  def visibleForOwner(o: Owner, map: BattleMap): Set[MapPoint] = map.objects.collect {
    case (mp, CombatUnitObject(u)) if u.owner == o => mp -> u.visionRange
    // TODO replace with injections
  }.flatMap(Function.tupled(new FovCalculator(map).getVisiblePointsFrom))
      .toSet
}
