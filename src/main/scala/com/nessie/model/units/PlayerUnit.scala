package com.nessie.model.units

trait PlayerUnit extends CombatUnit {
  override type T <: PlayerUnit
  val owner: Owner = Owner.Player
}
