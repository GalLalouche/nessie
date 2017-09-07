package com.nessie.model.units

import com.nessie.model.units.abilities.{MeleeAttack, MoveAbility, UnitAbility}

trait CombatUnit extends HasHitPoints {
  override type T <: CombatUnit
  val owner: Owner
  def moveAbility: MoveAbility = MoveAbility(3)
  def attackAbility: MeleeAttack = MeleeAttack(1)
  def abilities: Traversable[UnitAbility] = List(moveAbility, attackAbility)
}
