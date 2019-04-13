package com.nessie.model.units.abilities

import com.nessie.gm.{Attack, GameStateChange, Movement}
import com.nessie.model.map.MapPoint

object AbilityToChange {
  def apply(ability: UnitAbility)(src: MapPoint, dst: MapPoint): GameStateChange = ability match {
    case MoveAbility(_) => Movement(src = src, dst = dst)
    case d: DamageAbility => Attack(src = src, dst = dst, damageAmount = d.damage)
  }
}
