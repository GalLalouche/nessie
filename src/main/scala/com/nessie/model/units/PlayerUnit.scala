package com.nessie.model.units

import com.nessie.model.units.inventory.Equipment
import monocle.Lens

trait PlayerUnit extends CombatUnit {
  override type T <: PlayerUnit
  val owner: Owner = Owner.Player
  val equipment: Equipment
  protected def withEquipment(eq: Equipment): T
}

object PlayerUnit {
  val equipment = Lens[PlayerUnit, Equipment](_.equipment)(eq => pu => pu withEquipment eq)
}
