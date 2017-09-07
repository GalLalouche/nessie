package com.nessie.model.units.inventory

class EquipmentOccupiedException(val equipSlot: EquipSlot)
    extends RuntimeException("Equipment is occupied in " + equipSlot)

