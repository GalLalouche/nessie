package com.nessie.model.units

import com.nessie.common.MonocleUtils
import com.nessie.model.units.abilities.UnitAbility
import monocle.Lens

case class Monster(
    hitPoints: HitPoints,
    metadata: CombatUnitMetadata,
    override val abilities: Seq[UnitAbility],
) extends CombatUnit {
  override val owner = Owner.AI
  override def hitPointsLens: Lens[CombatUnit, HitPoints] = MonocleUtils.unsafeCovariance(Monster.hitPoints)
}

object Monster {
  def hitPoints: Lens[Monster, HitPoints] = Lens[Monster, HitPoints](_.hitPoints)(hp => _.copy(hitPoints = hp))
}
