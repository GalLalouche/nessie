package com.nessie.gm

import com.nessie.common.MonocleUtils
import com.nessie.model.eq.EventQueue
import com.nessie.model.map.{BattleMap, CombatUnitObject}
import com.nessie.model.units.CombatUnit
import common.rich.RichT._
import monocle.{Lens, Setter}
import monocle.macros.Lenses

import scalaz.std.OptionInstances

@Lenses
case class GameState(
    map: BattleMap,
    eq: EventQueue[Event],
    currentTurn: Option[ComposedTurn], // True after a unit has moved but not finished its turn yet.
) extends OptionInstances {
  //TODO handle the case where the unit dies
  private def mapUnit(original: CombatUnit, replacer: CombatUnit => CombatUnit): GameState = {
    val noneIfDead = replacer(original).opt.filterNot(_.hitPoints.isDead)
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
    val units = map.objects.map(_._2).flatMap(_.safeCast[CombatUnitObject]).map(_.unit)
    val eq = units.foldLeft(new EventQueue[Event]) {
      (agg, next) => agg.add(UnitTurn(next), 1.0 / next.moveAbility.range)
    }
    GameState(map, eq, currentTurn = None)
  }
  def unitSetter(original: CombatUnit): Setter[GameState, CombatUnit] =
    Setter[GameState, CombatUnit](replacer => _.mapUnit(original, replacer))
}
