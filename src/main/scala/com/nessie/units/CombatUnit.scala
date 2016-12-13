package com.nessie.units

import com.nessie.units.abilities.{MeleeAttack, MoveAbility, UnitAbility}

trait CombatUnit extends HasHitPoints {
  override type T <: CombatUnit
  val owner: Owner
  def abilities: Traversable[UnitAbility] = List(new MoveAbility(3), MeleeAttack(3))
}
