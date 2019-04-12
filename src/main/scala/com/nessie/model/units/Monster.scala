package com.nessie.model.units

import monocle.Lens

case class Monster(
    hitPoints: HitPoints,
    metadata: CombatUnitMetadata,
) extends CombatUnit {
  override val owner = Owner.AI
}

object Monster {
  def hitPoints: Lens[Monster, HitPoints] = Lens[Monster, HitPoints](_.hitPoints)(hp => _.copy(hitPoints = hp))
}
