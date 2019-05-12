package com.nessie.model.units

import com.nessie.model.units.abilities.{MeleeAttack, MoveAbility}

object Zombie {
  def create: Monster = new Monster(
    HitPoints.fullHp(20),
    CombatUnitMetadata("Zombie"),
    Vector(MoveAbility(1), MeleeAttack(5)),
  )
}
