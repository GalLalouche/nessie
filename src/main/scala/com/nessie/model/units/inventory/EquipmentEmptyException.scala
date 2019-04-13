package com.nessie.model.units.inventory

class EquipmentEmptyException(val equipSlot: EquipType)
    extends IllegalArgumentException("Equipment is empty in " + equipSlot)
