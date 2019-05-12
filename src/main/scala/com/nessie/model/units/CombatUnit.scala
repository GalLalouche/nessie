package com.nessie.model.units

import com.nessie.model.units.abilities.{DamageAbility, MeleeAttack, MoveAbility, UnitAbility}
import common.rich.func.{MoreIterableInstances, ToMoreMonadPlusOps}
import monocle.Lens

trait CombatUnit extends ToMoreMonadPlusOps with MoreIterableInstances {
  def hitPoints: HitPoints
  // It is assumed that the actual source (its S) passed to the lens, will be of the correct type, e.g., for
  // PlayerUnit, this type would be PlayerUnit. Otherwise, as ClassCastException will be thrown.
  def hitPointsLens: Lens[CombatUnit, HitPoints]
  def metadata: CombatUnitMetadata
  def owner: Owner
  def moveAbility: MoveAbility = MoveAbility(3)
  def attackAbility: MeleeAttack = MeleeAttack(1)
  def abilities: Iterable[UnitAbility] = Vector(moveAbility, attackAbility)
  def moveAbilities: Iterable[MoveAbility] = abilities.select[MoveAbility]
  def attacks: Iterable[DamageAbility] = abilities.select[DamageAbility]
}
