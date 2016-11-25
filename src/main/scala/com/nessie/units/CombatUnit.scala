package com.nessie.units

trait CombatUnit extends HasHitPoints {
  override type T <: CombatUnit
  val owner: Owner
  def getBasicAttack = Attack(5)
}
