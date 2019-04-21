package com.nessie.model.units.abilities

import com.nessie.gm.{Attack, GameStateChange, Movement}
import com.nessie.model.map.MapPoint

object AbilityToChange {
  private val MaxMovementDelay = 1.0
  def apply(ability: UnitAbility)(src: MapPoint, dst: MapPoint): GameStateChange = ability match {
    case MoveAbility(r) =>
      Movement(src = src, dst = dst, turnDelay = MaxMovementDelay * src.manhattanDistanceTo(dst).toDouble / r)
    case d: DamageAbility => Attack(src = src, dst = dst, damageAmount = d.damage, turnDelay = 1.0)
  }
}
