package com.nessie.map.objects

import com.nessie.model.map.objects.BattleMapObject
import com.nessie.units.CombatUnit

class CombatUnitObject(val u: CombatUnit) extends BattleMapObject {
	require(u != null)
}
object CombatUnitObject {
	def unapply(e: CombatUnitObject) = Some(e.u) 
} 