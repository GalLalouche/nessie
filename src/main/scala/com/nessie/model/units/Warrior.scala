package com.nessie.model.units

import com.nessie.model.units.abilities.{MeleeAttack, MoveAbility}

object Warrior {
  def create: PlayerUnit = new PlayerUnit(
    HitPoints.fullHp(10),
    CombatUnitMetadata("Warrior"),
  ) {
    override val abilities = Vector(MoveAbility(3), MeleeAttack(5))
  }
}
