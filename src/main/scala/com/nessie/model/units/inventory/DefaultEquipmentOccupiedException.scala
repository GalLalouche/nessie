package com.nessie.model.units.inventory

class DefaultEquipmentOccupiedException(val equipType: EquipType)
    extends IllegalArgumentException("Equipment is occupied in all " + equipType + " slots")

