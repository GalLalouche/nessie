package com.nessie.model.units.inventory

class EquipmentOccupiedException(val equipSlot: EquipSlot)
    extends IllegalArgumentException("Equipment is occupied in " + equipSlot)

