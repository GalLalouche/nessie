package com.nessie.model.map

import com.nessie.model.units.CombatUnit
import monocle.Lens
import monocle.macros.Lenses

@Lenses
case class CombatUnitObject(unit: CombatUnit) extends BattleMapObject

object CombatUnitObject {
  def findIn(u: CombatUnit, map: BattleMap): Option[MapPoint] = map.objects.collectFirst {
    case (p, CombatUnitObject(unit)) if unit == u => p
  }
  def lens(u: CombatUnit): Lens[BattleMap, Option[CombatUnitObject]] = {
    def get(map: BattleMap) = findIn(u, map).map(map.apply).map(_.asInstanceOf[CombatUnitObject])
    def set(replace: Option[CombatUnitObject])(map: BattleMap) = findIn(u, map)
        .map {currentLocation =>
          replace match {
            case None => map.remove(currentLocation)
            case Some(r) => map.place(currentLocation, r)
          }
        } getOrElse map
    Lens[BattleMap, Option[CombatUnitObject]](get)(set)
  }
}
