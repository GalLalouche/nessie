package com.nessie.model.units.inventory

class InvalidSlotException(val item: EquippableItem, val attemptedSlot: EquipSlot)
    extends RuntimeException(s"Cannot equip <$item> in <$attemptedSlot> since its equipment type is <${item.equipType }>")
