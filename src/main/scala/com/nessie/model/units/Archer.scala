package com.nessie.model.units

import com.nessie.model.units.abilities.{RangedAttack, UnitAbility}

class Archer private(val currentHp: Int) extends CombatUnit {
  def this() = this(Archer.maxHp)
  override type T = Archer
  override protected def withHp(amount: Int): Archer = new Archer(amount)
  def rangedAttack: RangedAttack = RangedAttack(3, 3)
  override def abilities: Traversable[UnitAbility] = rangedAttack :: super.abilities.toList
  override val owner: Owner = Owner.Player
  override val maxHp: Int = Archer.maxHp
}

object Archer {
  private val maxHp = 7
}

