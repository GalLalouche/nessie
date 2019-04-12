package com.nessie.model.units

import com.nessie.common.MonocleUtils
import monocle.Lens

case class Monster(
    hitPoints: HitPoints,
    metadata: CombatUnitMetadata,
) extends CombatUnit {
  override val owner = Owner.AI
  override def hitPointsLens: Lens[CombatUnit, HitPoints] = MonocleUtils.unsafeCovariance(Monster.hitPoints)
}

object Monster {
  def hitPoints: Lens[Monster, HitPoints] = Lens[Monster, HitPoints](_.hitPoints)(hp => _.copy(hitPoints = hp))
}
