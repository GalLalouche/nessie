package com.nessie.model.units.abilities

import com.nessie.gm.{Attack, Movement, TurnAction}
import com.nessie.gm.TurnAction.{ActualAction, MovementAction}
import com.nessie.model.map.MapPoint

object AbilityToTurnAction {
  def apply(ability: UnitAbility)(src: MapPoint, dst: MapPoint): TurnAction = ability match {
    case MoveAbility(_) => MovementAction(Movement(src = src, dst = dst))
    case d: DamageAbility =>
      ActualAction(Attack(src = src, dst = dst, damageAmount = d.damage, turnDelay = 1.0))
  }
}
