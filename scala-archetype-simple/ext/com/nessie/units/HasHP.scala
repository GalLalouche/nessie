package com.nessie.units

/**
 * Created with IntelliJ IDEA.
 * User: gal
 * Date: 04/06/13
 * Time: 23:04
 * To change this template use File | Settings | File Templates.
 */
class HasHP private(val currentHp: Int, val maxHp: Int) {
	def healHp(i: Int) = {
		require (i >= 0)
		new HasHP (Math.min (currentHp + i, maxHp), maxHp)
	}

	require (currentHp >= 0)
	require (currentHp <= maxHp)
	require (maxHp > 0)


	def this(maxHp: Int) = this (maxHp, maxHp)

	def reduceHp(i: Int) = {
		require (i >= 0)
		new HasHP (Math.max (maxHp - i, 0), maxHp)
	}
}
