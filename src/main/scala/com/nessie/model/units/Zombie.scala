package com.nessie.model.units

import com.nessie.model.units.abilities.{MeleeAttack, MoveAbility}

class Zombie private(val currentHp: Int) extends CombatUnit {
  def this() = this(Zombie.maxHp)
  override type T = Zombie
  override protected def withHp(amount: Int): Zombie = new Zombie(amount)

  override def moveAbility: MoveAbility = MoveAbility(1)
  override def attackAbility: MeleeAttack = MeleeAttack(5)
  override val owner: Owner = Owner.AI
  override val maxHp: Int = Zombie.maxHp
}

object Zombie {
  val maxHp = 20
}
