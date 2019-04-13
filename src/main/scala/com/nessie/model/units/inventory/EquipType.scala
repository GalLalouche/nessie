package com.nessie.model.units.inventory

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed trait EquipType extends EnumEntry

object EquipType extends Enum[EquipType] {
  val values: immutable.IndexedSeq[EquipType] = findValues

  case object Head extends EquipType
  case object Torso extends EquipType
  case object Hand extends EquipType
}
