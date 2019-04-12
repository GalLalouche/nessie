package com.nessie.model.units

import com.nessie.common.MonocleUtils
import monocle.Lens

case class PlayerUnit(
    hitPoints: HitPoints,
    metadata: CombatUnitMetadata,
) extends CombatUnit {
  override val owner = Owner.Player
  override def hitPointsLens: Lens[CombatUnit, HitPoints] = MonocleUtils.unsafeCovariance(PlayerUnit.hitPoints)
}

object PlayerUnit {
  def hitPoints: Lens[PlayerUnit, HitPoints] =
    Lens[PlayerUnit, HitPoints](_.hitPoints)(hp => _.copy(hitPoints = hp))
}

