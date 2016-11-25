package com.nessie.map

import com.nessie.units.CombatUnit
import monocle.macros.Lenses

@Lenses
case class CombatUnitObject(unit: CombatUnit) extends BattleMapObject
