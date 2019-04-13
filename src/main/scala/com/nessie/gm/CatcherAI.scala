package com.nessie.gm

import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.{CombatUnit, Owner}
import com.nessie.model.units.abilities.{AbilityToChange, CanBeUsed}
import common.rich.RichT._

private class CatcherAI extends AI {
  //TODO cache
  private def distanceToPlayer(map: BattleMap, point: MapPoint): Int = map.points
      .flatMap(e => e._2.safeCast[CombatUnitObject].map(e._1 -> _))
      .filter(_._2.unit.owner == Owner.Player)
      .map(_._1.manhattanDistanceTo(point))
      .min
  override def apply(u: CombatUnit)(gs: GameState): GameStateChange = {
    val map = gs.map
    val unitLocation = CombatUnitObject.findIn(u, map).get
    val attack: Option[GameStateChange] = {
      val attackAbility = u.attackAbility
      map.points
          .map(_._1)
          .find(CanBeUsed(attackAbility)(map, unitLocation, _))
          .map(AbilityToChange(attackAbility)(unitLocation, _))
    }
    lazy val move: GameStateChange = {
      val moveAbility = u.moveAbility
      map.points.map(_._1)
          .filter(CanBeUsed(moveAbility)(map, unitLocation, _))
          .minBy(distanceToPlayer(map, _))
          .mapTo(AbilityToChange(moveAbility)(unitLocation, _))
    }
    attack.getOrElse(move)
  }
}

