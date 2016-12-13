package com.nessie.gm

import com.nessie.common.MonocleUtils.castingIso
import com.nessie.map.model.{BattleMap, MapPoint}
import com.nessie.map.{BattleMapObject, CombatUnitObject}
import com.nessie.units.abilities.DamageAbility
import com.nessie.units.{CombatUnit, Owner}
import common.rich.collections.RichTraversableOnce._
import common.rich.func.RichMonadPlus._
import common.rich.func.MoreMonadPlus._

private class CatcherAI {
  def apply(u: CombatUnit)(gs: GameState): GameState = {
    val map = gs.map
    val unitLocation: MapPoint = CombatUnitObject.findIn(u, map).get
    val playerLocation = map.points.collect {
      case (p, CombatUnitObject(unit)) if unit.owner == Owner.Player => p
    }.single
    val f: GameState => GameState = if (playerLocation.manhattanDistanceTo(unitLocation) == 1) {
      u.abilities.select[DamageAbility].head.applyTo(unitLocation, playerLocation)
    } else {
      def newLocation: MapPoint = {
        if (playerLocation.y != unitLocation.y)
          unitLocation.copy(y = unitLocation.y + (if (playerLocation.y > unitLocation.y) 1 else -1))
        else
          unitLocation.copy(x = unitLocation.x + (if (playerLocation.x > unitLocation.x) 1 else -1))
      }
      GameState.map.modify(_.move(unitLocation).to(newLocation))
    }
    f(gs)
  }
}

