package com.nessie.model.units.abilities

sealed trait UnitAbility {
  def name: String
}

case class MoveAbility(range: Int) extends UnitAbility {
  override val name = "Move to"
}

trait DamageAbility extends UnitAbility {
  def damage: Int
}
case class MeleeAttack(override val damage: Int) extends DamageAbility {
  override val name = "Melee"
}
case class RangedAttack(override val damage: Int, range: Int) extends DamageAbility {
  override val name = "Ranged"
}
