package com.nessie.model.units.inventory

sealed trait EquipType
object EquipType {
  object Head extends EquipType
  object Torso extends EquipType
  object Hand extends EquipType
}
