package com.nessie.model.units

class Skeleton private(val currentHp: Int) extends CombatUnit {
  def this() = this(Skeleton.maxHp)
  override type T = Skeleton
  override protected def withHp(amount: Int): Skeleton = new Skeleton(amount)
  override val owner: Owner = Owner.AI
  override val maxHp: Int = Skeleton.maxHp
}

object Skeleton {
  val maxHp = 5
}
