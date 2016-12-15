package com.nessie.model.units

class Warrior private(val currentHp: Int) extends CombatUnit {
  def this() = this(Warrior.maxHp)
  override type T = Warrior
  override protected def withHp(amount: Int): Warrior = new Warrior(amount)
  override val owner: Owner = Owner.Player
  override val maxHp: Int = Warrior.maxHp
}

object Warrior {
  private val maxHp = 10
}
