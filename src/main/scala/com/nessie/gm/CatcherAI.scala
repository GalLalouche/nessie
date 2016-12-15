package com.nessie.gm

import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.{CombatUnit, Owner}
import common.rich.RichT._

private class CatcherAI {
  //TODO cache
  private def distanceToPlayer(map: BattleMap, point: MapPoint): Int = map.points
      .flatMap(e => e._2.safeCast[CombatUnitObject].map(e._1 -> _))
      .filter(_._2.unit.owner == Owner.Player)
      .map(_._1.manhattanDistanceTo(point))
      .min
  def apply(u: CombatUnit)(gs: GameState): GameState = {
    val map = gs.map
    val unitLocation = CombatUnitObject.findIn(u, map).get
    val attack: Option[GameState => GameState] = {
      val attackAbility = u.attackAbility
      map.points
          .map(_._1)
          .find(attackAbility.canBeUsed(map, unitLocation, _))
          .map(attackAbility.applyTo(unitLocation, _))
    }
    lazy val move: GameState => GameState = {
      val moveAbility = u.moveAbility
      map.points.map(_._1)
          .filter(moveAbility.canBeUsed(map, unitLocation, _))
          .minBy(distanceToPlayer(map, _))
          .mapTo(moveAbility.applyTo(unitLocation, _))
    }
    attack.getOrElse(move)(gs)
  }
}

