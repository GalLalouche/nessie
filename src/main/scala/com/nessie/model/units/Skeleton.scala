package com.nessie.model.units

import com.nessie.model.units.abilities.{MeleeAttack, MoveAbility}

object Skeleton {
  def create: Monster = new Monster(
    HitPoints.fullHp(5),
    CombatUnitMetadata("Skeleton"),
  ) {
    override val abilities = Vector(MoveAbility(3), MeleeAttack(2))
  }
}
