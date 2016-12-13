package com.nessie.units.abilities

case class MeleeAttack(amount: Int) extends DamageAbility(amount) with MeleeAbility with UnitAbility
