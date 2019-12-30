package com.nessie.model.units.inventory

import org.scalatest.FreeSpec

import common.test.AuxSpecs

class EquipmentTest extends FreeSpec with AuxSpecs {
  private object Helmet extends EquippableItem(EquipType.Head, "helmet")
  object Sword extends EquippableItem(EquipType.Hand, "sword")
  object Shield extends EquippableItem(EquipType.Hand, "shield")
  "Exceptions" - {
    val $ = Equipment() equip Helmet
    "defaults" - {
      "equip used" in {
        object Crown extends EquippableItem(EquipType.Head, "crown")
        the[DefaultEquipmentOccupiedException].thrownBy({$ equip Crown})
            .equipType shouldReturn EquipType.Head
      }
      "equip reusing defaults" in {
        the[DefaultEquipmentOccupiedException]
            .thrownBy({Equipment() equip Sword equip Shield equip Sword})
            .equipType shouldReturn EquipType.Hand
      }
    }
    "equip on wrong slot" in {
      val e = the[InvalidSlotException] thrownBy {Equipment().equip(Sword, EquipSlot.Head)}
      e.item shouldReturn Sword
      e.attemptedSlot shouldReturn EquipSlot.Head
    }
    "unequip on none-existing" in {
      the[EquipmentOccupiedException].thrownBy({$ unequip EquipSlot.Torso})
          .equipSlot shouldReturn EquipSlot.Torso
    }
  }
  "equip" in {
    Equipment().equip(Helmet)(EquipSlot.Head).get shouldReturn Helmet
  }
  "defaults" in {
    val e = Equipment().equip(Sword).equip(Shield)
    e(EquipSlot.RightHand).get shouldReturn Sword
    e(EquipSlot.LeftHand).get shouldReturn Shield
  }
  "Can handle multiple equipments of same type" in {
    val e = Equipment().equip(Sword, EquipSlot.RightHand).equip(Shield, EquipSlot.LeftHand)
    e(EquipSlot.RightHand).get shouldReturn Sword
    e(EquipSlot.LeftHand).get shouldReturn Shield
  }
  "unequip" in {
    Equipment().equip(Helmet).unequip(EquipSlot.Head)(EquipSlot.Head) shouldReturn None
  }
  "allSlots" in {
    Equipment().equip(Helmet, EquipSlot.Head).equip(Shield, EquipSlot.LeftHand)
        .allSlots shouldReturn Seq(
      EquipSlot.Head -> Some(Helmet),
      EquipSlot.Torso -> None,
      EquipSlot.RightHand -> None,
      EquipSlot.LeftHand -> Some(Shield),
    )
  }
}
