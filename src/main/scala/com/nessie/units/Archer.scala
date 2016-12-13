package com.nessie.units

class Archer private(val currentHp: Int) extends CombatUnit {
  def this() = this(Archer.maxHp)
  override type T = Archer
  override protected def withHp(amount: Int): Archer = new Archer(amount)
  override val owner: Owner = Owner.Player
  override val maxHp: Int = Archer.maxHp
}

object Archer {
  private val maxHp = 10
}

