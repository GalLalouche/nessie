package com.nessie.model.units.abilities

case class RangedAttack(range: Int, damage: Int) extends DamageAbility with RangedAbility
