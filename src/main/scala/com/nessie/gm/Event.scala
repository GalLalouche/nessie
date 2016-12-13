package com.nessie.gm

import com.nessie.units.CombatUnit
import monocle.macros.Lenses

sealed trait Event
@Lenses
case class UnitTurn(u: CombatUnit) extends Event
