package com.nessie.gm

import com.nessie.common.MonocleUtils
import com.nessie.model.eq.EventQueue
import com.nessie.model.map.{BattleMap, CombatUnitObject}
import com.nessie.model.map.fov.{FogOfWar, TeamFov}
import com.nessie.model.units.CombatUnit

import scalaz.std.option.optionInstance
import monocle.syntax.apply._
import monocle.Setter

import common.rich.RichT._

private trait UnitMapper {
  def mapAndFogs: MapAndFogs => MapAndFogs
  def eq: EventQueue[Event] => EventQueue[Event]
}

private object UnitMapper {
  private def updateEq(
      original: CombatUnit)(
      mod: Option[CombatUnit] => Option[CombatUnit])(
      eq: EventQueue[Event]): EventQueue[Event] = {
    mod(original.opt) match {
      case None => eq.remove {
        case UnitTurn(u) if u == original => true
      }
      case Some(r) => eq.partialMap {
        case UnitTurn(u) if u == original => UnitTurn(r)
      }
    }
  }
  def apply(original: CombatUnit, replacer: CombatUnit => CombatUnit): UnitMapper = new UnitMapper {
    private val noneIfDead = replacer(original).optFilter(_.hitPoints.isNotDead)
    override def mapAndFogs = mapf => {
      val owner = original.owner
      val mapLens: Setter[BattleMap, Option[CombatUnit]] =
        CombatUnitObject.lens(original).asSetter.^|->(MonocleUtils.lift(CombatUnitObject.unit)(optionInstance))
      val updatedMap = MapAndFogs.map.composeSetter(mapLens).set(noneIfDead)(mapf)
      val updatedFogOfWar: FogOfWar => FogOfWar =
        _.mapIf(noneIfDead.isEmpty).to(_.updateVisible(TeamFov.visibleForOwner(owner, updatedMap.map)))
      updatedMap.&|->(MapAndFogs.fogForOwner(owner)).modify(updatedFogOfWar)
    }
    override def eq =
      Setter[EventQueue[Event], Option[CombatUnit]](updateEq(original)).set(noneIfDead)
  }
}
