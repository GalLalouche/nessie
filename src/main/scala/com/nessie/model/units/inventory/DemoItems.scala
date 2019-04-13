package com.nessie.model.units.inventory

object DemoItems {
  val sword = EquippableItem(EquipType.Hand, "Sword")
  val shield = EquippableItem(EquipType.Hand, "Shield")
  // TODO handle two handed
  val bow = EquippableItem(EquipType.Hand, "Bow")
  val helmet = EquippableItem(EquipType.Head, "Helmet")
  val chainMail = EquippableItem(EquipType.Torso, "Chain Mail")
  val leatherArmor = EquippableItem(EquipType.Torso, "Leather Armor")
}
