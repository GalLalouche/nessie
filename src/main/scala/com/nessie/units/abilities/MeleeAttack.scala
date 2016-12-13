package com.nessie.units.abilities

case class MeleeAttack(amount: Int) extends MeleeAbility with DamageAbility with UnitAbility
