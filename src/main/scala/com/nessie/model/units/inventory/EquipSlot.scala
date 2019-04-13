package com.nessie.model.units.inventory

import common.rich.collections.RichTraversableOnce._
import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

/**
 * An equipment slot on a character, e.g., head, torso, or left/right hand.
 * Every slot has a type, but there can be several slots for each type: e.g., left/right
 * hand, two rings, etc.
 */
sealed abstract class EquipSlot(val equipType: EquipType) extends EnumEntry

object EquipSlot extends Enum[EquipSlot] {
  val values: immutable.IndexedSeq[EquipSlot] = findValues

  case object Head extends EquipSlot(EquipType.Head)
  case object Torso extends EquipSlot(EquipType.Torso)
  case object RightHand extends EquipSlot(EquipType.Hand)
  case object LeftHand extends EquipSlot(EquipType.Hand)

  val default: EquipType => EquipSlot = values.toMultiMap(_.equipType).mapValues(_.head)
}
