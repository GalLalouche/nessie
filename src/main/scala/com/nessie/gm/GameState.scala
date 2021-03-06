package com.nessie.gm

import com.nessie.common.MonocleUtils
import com.nessie.model.eq.EventQueue
import com.nessie.model.map.{BattleMap, CombatUnitObject}
import com.nessie.model.map.fov.FogOfWar
import com.nessie.model.units.CombatUnit
import common.rich.RichT._
import monocle.{Lens, Setter}
import monocle.macros.Lenses

import scalaz.std.OptionInstances

@Lenses
case class GameState(
    fogOfWar: FogOfWar,
    eq: EventQueue[Event],
    currentTurn: Option[ComposedTurn], // Is defined when it's time for a unit to take its turn.
) extends OptionInstances {
  def map: BattleMap = fogOfWar.map
  //TODO handle the case where the unit dies
  private def mapUnit(original: CombatUnit, replacer: CombatUnit => CombatUnit): GameState = {
    val noneIfDead = replacer(original).optFilter(_.hitPoints.isNotDead)
    val mapLens: Lens[BattleMap, Option[CombatUnit]] =
      CombatUnitObject.lens(original).^|->(MonocleUtils.lift(CombatUnitObject.unit)(optionInstance))
    val queueLens = Setter[EventQueue[Event], Option[CombatUnit]](GameState.updateEq(original))
    GameState.map.modify(mapLens.set(noneIfDead))
        .andThen(GameState.eq.modify(queueLens.set(noneIfDead)))
        .apply(this)
  }
}
object GameState {
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
  def fromMap(map: BattleMap): GameState = {
    val fow = FogOfWar.allVisible(map)
    val units = map.objects.map(_._2).flatMap(_.safeCast[CombatUnitObject]).map(_.unit)
    val eq = units.foldLeft(new EventQueue[Event]) {
      (agg, next) => agg.add(UnitTurn(next), 1.0 / next.moveAbility.range)
    }
    GameState(fow, eq, currentTurn = None)
  }
  def unitSetter(original: CombatUnit): Setter[GameState, CombatUnit] =
    Setter[GameState, CombatUnit](replacer => _.mapUnit(original, replacer))
  def map: Lens[GameState, BattleMap] = fogOfWar ^|-> FogOfWar.map
}
