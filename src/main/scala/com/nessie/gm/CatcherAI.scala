package com.nessie.gm

import com.nessie.map.model.{BattleMap, MapPoint}
import com.nessie.map.{BattleMapObject, CombatUnitObject}
import com.nessie.units.{CombatUnit, Owner}
import common.rich.collections.RichTraversableOnce._
import monocle.Iso

private class CatcherAI {
  // TODO Lens utils? Should be prism?
  private def castingIso[A, B <: A] = Iso[A, B](_.asInstanceOf[B])(identity)
  def apply(u: CombatUnit)(map: BattleMap): BattleMap = {
    val unitLocation: MapPoint = map.points.collect {
      case (p, CombatUnitObject(unit)) if unit == u => p
    }.single
    val playerLocation = map.points.collect {
      case (p, CombatUnitObject(unit)) if unit.owner == Owner.Player => p
    }.single
    if (playerLocation.manhattanDistanceTo(unitLocation) == 1) {
      val combatUnitObject = BattleMap.pointLens(playerLocation)
          .^<->(castingIso[BattleMapObject, CombatUnitObject])
      combatUnitObject.^|->(CombatUnitObject.unit).modify(_.getAttacked(u.getBasicAttack))(map)
    } else {
      def newLocation: MapPoint = {
        if (playerLocation.y != unitLocation.y)
          unitLocation.copy(y = unitLocation.y + (if (playerLocation.y > unitLocation.y) 1 else -1))
        else
          unitLocation.copy(x = unitLocation.x + (if (playerLocation.x > unitLocation.x) 1 else -1))
      }
      map.move(unitLocation).to(newLocation)
    }
  }
}

