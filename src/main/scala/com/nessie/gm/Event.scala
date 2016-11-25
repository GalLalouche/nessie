package com.nessie.gm

import com.nessie.units.CombatUnit

sealed trait Event
case class UnitTurn(next: CombatUnit) extends Event
