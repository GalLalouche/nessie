package com.nessie.units

class CombatUnit protected(currentHp: Int, maxHp: Int) extends HasHP(currentHp, maxHp) {
	def this(maxHp: Int) = this(maxHp, maxHp)

	override def reduceHp(i: Int): HasHP = CombatUnit(super.reduceHp(i))

	override def healHp(i: Int): HasHP = CombatUnit(super.healHp(i))

	def getBasicAttack = Attack(5)
}

object CombatUnit {
	private def apply(o: HasHP) = new CombatUnit(o.currentHp, o.maxHp)
}