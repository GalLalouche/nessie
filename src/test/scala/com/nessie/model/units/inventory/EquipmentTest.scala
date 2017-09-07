package com.nessie.model.units.inventory

import common.AuxSpecs
import org.scalatest.FreeSpec

class EquipmentTest extends FreeSpec with AuxSpecs {
  private object Helmet extends EquippableItem(EquipType.Head)
  object Sword extends EquippableItem(EquipType.Hand)
  object Shield extends EquippableItem(EquipType.Hand)
  "Exceptions" - {
    val $ = new Equipment() equip Helmet
    "equip used" in {
      object Crown extends EquippableItem(EquipType.Head)
      (the[EquipmentOccupiedException] thrownBy { $ equip Crown })
          .equipSlot shouldReturn EquipSlot.Head
    }
    "equip reusing defaults" in {
      (the[EquipmentOccupiedException] thrownBy { new Equipment() equip Sword equip Shield })
          .equipSlot.equipType shouldReturn EquipType.Hand
    }
    "equip on wrong slot" in {
      val e = the[InvalidSlotException] thrownBy { new Equipment().equip(Sword, EquipSlot.Head) }
      e.item shouldReturn Sword
      e.attemptedSlot shouldReturn EquipSlot.Head
    }
    "unequip on none-existing" in {
      (the[EquipmentOccupiedException] thrownBy { $ unequip EquipSlot.Torso })
          .equipSlot shouldReturn EquipSlot.Torso
    }
  }
  "equip" in {
    (new Equipment() equip Helmet).apply(EquipSlot.Head).get shouldReturn Helmet
  }
  "unequip" in {
    (new Equipment() equip Helmet unequip EquipSlot.Head).apply(EquipSlot.Head) shouldReturn None
  }
  "Can handle multiples" in {
    noException should be thrownBy
        new Equipment().equip(Sword, EquipSlot.RightHand).equip(Shield, EquipSlot.LeftHand)
  }
}
