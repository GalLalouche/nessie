package com.nessie.model.units

import com.nessie.model.units.abilities.{DamageAbility, MeleeAttack, MoveAbility, UnitAbility}
import common.rich.func.{MoreIterableInstances, ToMoreMonadPlusOps}

trait CombatUnit extends ToMoreMonadPlusOps with MoreIterableInstances {
  def hitPoints: HitPoints
  def metadata: CombatUnitMetadata
  def owner: Owner
  def moveAbility: MoveAbility = MoveAbility(3)
  def attackAbility: MeleeAttack = MeleeAttack(1)
  def abilities: Iterable[UnitAbility] = List(moveAbility, attackAbility)
  def moveAbilities: Iterable[MoveAbility] = abilities.select[MoveAbility]
  def attacks: Iterable[DamageAbility] = abilities.select[DamageAbility]
}
