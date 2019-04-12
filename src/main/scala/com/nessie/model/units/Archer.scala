package com.nessie.model.units

import com.nessie.model.units.abilities.{MeleeAttack, MoveAbility, RangedAttack}
import com.nessie.model.units.stats.Stats

object Archer {
  def create: PlayerUnit = new PlayerUnit(
    HitPoints.fullHp(7),
    CombatUnitMetadata("Archer"),
    Stats(1, 2, 3)
  ) {
    override val abilities = Vector(MoveAbility(3), MeleeAttack(1), RangedAttack(3, 3))
  }
}

