package com.nessie.model.units

import monocle.Lens

case class PlayerUnit(
    hitPoints: HitPoints,
    metadata: CombatUnitMetadata,
) extends CombatUnit {
  override val owner = Owner.Player
}

object PlayerUnit {
  def hitPoints: Lens[PlayerUnit, HitPoints] =
    Lens[PlayerUnit, HitPoints](_.hitPoints)(hp => _.copy(hitPoints = hp))
}

