package com.nessie.model.units

import com.nessie.model.units.abilities.{MeleeAttack, MoveAbility}
import com.nessie.model.units.stats.Stats

object Warrior {
  def create: PlayerUnit = new PlayerUnit(
    HitPoints.fullHp(10),
    CombatUnitMetadata("Warrior"),
    Stats(3, 2, 3),
  ) {
    override val abilities = Vector(MoveAbility(3), MeleeAttack(5))
  }
}
