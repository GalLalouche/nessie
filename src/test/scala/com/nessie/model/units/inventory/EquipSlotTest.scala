package com.nessie.model.units.inventory

import org.scalatest.FreeSpec

import common.rich.collections.RichTraversableOnce._
import common.test.AuxSpecs

class EquipSlotTest extends FreeSpec with AuxSpecs {
  "default" in {
    EquipSlot.default(EquipType.Hand) shouldReturn Seq(EquipSlot.RightHand, EquipSlot.LeftHand)
    EquipSlot.default(EquipType.Head).single shouldReturn EquipSlot.Head
    EquipSlot.default(EquipType.Torso).single shouldReturn EquipSlot.Torso
  }
}
