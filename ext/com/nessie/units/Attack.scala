package com.nessie.units

class Attack(val damage: Int) {
	require(damage >= 0)
}

object Attack {
	def apply(d: Int) = new Attack(d)
}
