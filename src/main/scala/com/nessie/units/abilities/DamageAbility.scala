package com.nessie.units.abilities

import com.nessie.gm.GameState
import com.nessie.map.CombatUnitObject
import com.nessie.map.model.{BattleMap, MapPoint}
import com.nessie.units.CombatUnit
import common.rich.RichT._

class DamageAbility(amount: Int) extends UnitAbility {
  addConstraint(new CanBeUsed {
    override def apply(battleMap: BattleMap, source: MapPoint, destination: MapPoint): Boolean = {
      def getOwner(point: MapPoint) = battleMap(point).safeCast[CombatUnitObject].map(_.unit.owner)
      val $ = for (sOwner <- getOwner(source); dOwner <- getOwner(destination))
        yield sOwner != dOwner
      $ getOrElse false
    }
  })
  private def getUnit(map: BattleMap, point: MapPoint): CombatUnit =
    map(point).asInstanceOf[CombatUnitObject].unit
  override def applyTo(source: MapPoint, destination: MapPoint): (GameState) => GameState =
    gs => GameState.unitSetter(getUnit(gs.map, destination)).modify(_.reduceHp(amount))(gs)
}
