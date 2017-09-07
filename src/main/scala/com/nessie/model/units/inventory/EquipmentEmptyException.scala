package com.nessie.model.units.inventory

class EquipmentEmptyException(val equipSlot: EquipType)
    extends RuntimeException("Equipment is empty in " + equipSlot)
