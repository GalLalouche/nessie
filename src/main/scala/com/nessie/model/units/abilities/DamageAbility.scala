package com.nessie.model.units.abilities

import com.nessie.gm.GameState
import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.{CombatUnit, Monster, PlayerUnit}
import common.rich.RichT._
import monocle.syntax.ApplySyntax

trait DamageAbility extends UnitAbility with ApplySyntax {
  def damage: Int
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
  override def applyTo(source: MapPoint, destination: MapPoint): GameState => GameState = gs => {
    GameState.unitSetter(getUnit(gs.map, destination)).modify {
      case m: Monster => m.&|->(Monster.hitPoints).modify(_.reduceHp(damage))
      case p: PlayerUnit => p.&|->(PlayerUnit.hitPoints).modify(_.reduceHp(damage))
    }(gs)
  }
}
