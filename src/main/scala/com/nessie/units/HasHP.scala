package com.nessie.units

import com.nessie.model.map.objects.BattleMapObject

class HasHP protected(val currentHp: Int, val maxHp: Int) extends BattleMapObject {
	def isDead = currentHp == 0

	require(currentHp >= 0)
	require(currentHp <= maxHp)
	require(maxHp > 0)

	def this(maxHp: Int) = this(maxHp, maxHp)

	def reduceHp(i: Int) = {
		require(i >= 0)
		new HasHP(Math.max(maxHp - i, 0), maxHp)
	}

	def healHp(i: Int) = {
		require(i >= 0)
		new HasHP(Math.min(currentHp + i, maxHp), maxHp)
	}

	def getAttacked(a: Attack) = {
		require(a != null)
		reduceHp(a.damage)
	}
}
