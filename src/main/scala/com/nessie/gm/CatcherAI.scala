package com.nessie.gm

import com.google.common.annotations.VisibleForTesting
import com.nessie.gm.TurnAction.EndTurn
import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.{CombatUnit, Owner}
import com.nessie.model.units.abilities.{AbilityToTurnAction, CanBeUsed}
import common.rich.RichT._

/** A dead simple AI that runs up to the nearest player and punches them in face. */
private class CatcherAI @VisibleForTesting() private[gm](c: AbilityToTurnActionConverter) extends AI {
  //TODO cache
  private def distanceToPlayer(map: BattleMap, point: MapPoint): Int = map.objects
      .flatMap(e => e._2.safeCast[CombatUnitObject].map(e._1 -> _))
      .filter(_._2.unit.owner == Owner.Player)
      .map(_._1.manhattanDistanceTo(point))
      .min
  override def apply(u: CombatUnit)(gs: GameState): TurnAction = {
    val currentTurn = gs.currentTurn.get
    val map = gs.map
    val unitLocation = CombatUnitObject.findIn(u, map).get
    val attack: Option[TurnAction] = {
      val attackAbility = u.attackAbility
      CanBeUsed.getUsablePoints(attackAbility)(map, unitLocation)
          .headOption
          .map(c(attackAbility)(unitLocation, _))
    }.filter(currentTurn.canAppendAction.const)
    lazy val moveOrEnd: TurnAction =
      if (distanceToPlayer(map, unitLocation) == 1)
        EndTurn
      else {
        val moveAbility = currentTurn.remainingMovementAbility
        val moveLocations = CanBeUsed.getUsablePoints(moveAbility)(map, unitLocation)
        if (moveLocations.isEmpty) EndTurn else moveLocations
            .minBy(distanceToPlayer(map, _))
            .mapTo(c(moveAbility)(unitLocation, _))
      }
    attack.getOrElse(moveOrEnd)
  }
}

private object CatcherAI extends CatcherAI(AbilityToTurnAction)

