package com.nessie.model.units.inventory

import scalaz.syntax.functor.ToFunctorOps
import common.rich.func.MoreEnumeratumInstances._

class Equipment private(equippedItems: Map[EquipSlot, EquippableItem]) {
  def apply(es: EquipSlot): Option[EquippableItem] = equippedItems get es
  def equip(item: EquippableItem): Equipment = {
    val defaults = EquipSlot.default(item.equipType)
    val slot = defaults.find(this.apply(_).isEmpty)
    if (slot.isEmpty)
      throw new DefaultEquipmentOccupiedException(item.equipType)
    equip(item, slot.get)
  }
  def equip(item: EquippableItem, equipSlot: EquipSlot): Equipment = {
    if (item.equipType != equipSlot.equipType) throw new InvalidSlotException(item, equipSlot)
    if (equippedItems contains equipSlot) throw new EquipmentOccupiedException(equipSlot)
    else new Equipment(equippedItems + (equipSlot -> item))
  }
  def unequip(es: EquipSlot): Equipment =
    if (!equippedItems.contains(es)) throw new EquipmentOccupiedException(es)
    else new Equipment(equippedItems - es)
  // used by lenses
  def strip = Equipment()
  def allSlots: Seq[(EquipSlot, Option[EquippableItem])] = EquipSlot.values.fproduct(equippedItems.get)
}

object Equipment {
  def apply() = new Equipment(Map())
  val empty: Equipment = apply()
}
