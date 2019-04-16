package com.nessie.model.units.inventory

import common.AuxSpecs
import common.rich.collections.RichTraversableOnce._
import org.scalatest.FreeSpec

class EquipSlotTest extends FreeSpec with AuxSpecs {
  "default" in {
    EquipSlot.default(EquipType.Hand) shouldReturn Seq(EquipSlot.RightHand, EquipSlot.LeftHand)
    EquipSlot.default(EquipType.Head).single shouldReturn EquipSlot.Head
    EquipSlot.default(EquipType.Torso).single shouldReturn EquipSlot.Torso
  }
}
