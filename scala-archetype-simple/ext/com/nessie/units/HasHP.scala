package com.nessie.units

class HasHP protected(val currentHp: Int, val maxHp: Int) {
	require (currentHp >= 0)
	require (currentHp <= maxHp)
	require (maxHp > 0)

	def this(maxHp: Int) = this (maxHp, maxHp)

	def reduceHp(i: Int) = {
		require (i >= 0)
		new HasHP (Math.max (maxHp - i, 0), maxHp)
	}

	def healHp(i: Int) = {
		require (i >= 0)
		new HasHP (Math.min (currentHp + i, maxHp), maxHp)
	}
}
