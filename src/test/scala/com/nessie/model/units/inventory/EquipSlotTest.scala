package com.nessie.model.units.inventory

import common.AuxSpecs
import org.scalatest.FreeSpec

class EquipSlotTest extends FreeSpec with AuxSpecs {
  "default" in {
    EquipSlot.default(EquipType.Hand) shouldReturn EquipSlot.RightHand
    EquipSlot.default(EquipType.Head) shouldReturn EquipSlot.Head
    EquipSlot.default(EquipType.Torso) shouldReturn EquipSlot.Torso
  }
}
