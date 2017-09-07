package com.nessie.model.units.inventory

class Equipment private(equippedItems: Map[EquipSlot, EquippableItem]) {
  def this() = this(Map())
  def apply(es: EquipSlot) = equippedItems get es
  def equip(item: EquippableItem): Equipment = equip(item, EquipSlot.default(item.equipType))
  def equip(item: EquippableItem, equipSlot: EquipSlot): Equipment = {
    if (item.equipType != equipSlot.equipType)
      throw new InvalidSlotException(item, equipSlot)
    if (equippedItems contains equipSlot) throw new EquipmentOccupiedException(equipSlot)
    else new Equipment(equippedItems + (equipSlot -> item))
  }
  def unequip(es: EquipSlot): Equipment =
    if (!equippedItems.contains(es)) throw new EquipmentOccupiedException(es)
    else new Equipment(equippedItems - es)
  // used by lenses
  def strip = new Equipment()
}
