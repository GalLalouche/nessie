package com.nessie.gm

import com.nessie.common.MonocleUtils
import com.nessie.events.model.EventQueue
import com.nessie.map.CombatUnitObject
import com.nessie.map.model.BattleMap
import com.nessie.units.CombatUnit
import common.rich.RichT._
import common.rich.func.MoreMonadPlus._
import common.rich.func.RichMonadPlus._
import monocle.macros.Lenses
import monocle.{Lens, Setter}

import scalaz.std.OptionInstances

@Lenses
case class GameState(map: BattleMap, eq: EventQueue[Event]) extends OptionInstances {
  //TODO handle the case where the unit dies
  private def mapUnit(original: CombatUnit, replacer: CombatUnit => CombatUnit): GameState = {
    val noneIfDead = Some(replacer(original)).filterNot(_.isDead)
    val mapLens: Lens[BattleMap, Option[CombatUnit]] =
      CombatUnitObject.lens(original).^|->(MonocleUtils.lift(CombatUnitObject.unit)(optionInstance))
    val queueLens = Setter[EventQueue[Event], Option[CombatUnit]](GameState.updateEq(original))
    GameState.map.modify(mapLens.set(noneIfDead))
        .andThen(GameState.eq.modify(queueLens.set(noneIfDead)))
        .apply(this)
  }
}
object GameState {
  private def updateEq(original: CombatUnit)(mod: Option[CombatUnit] => Option[CombatUnit])(eq: EventQueue[Event]): EventQueue[Event] = {
    mod(Some(original)) match {
      case None => eq.remove {
        case UnitTurn(u) if u == original => true
      }
      case Some(r) => eq.partialMap {
        case UnitTurn(u) if u == original => UnitTurn(r)
      }
    }
  }
  //
  //  }
  //    eq.map[Event] {
  //      case UnitTurn(u) if u == original =>
  //        val replacement = mod(Some(u))
  //        replacement
  //      case e => e
  //    }
  def fromMap(map: BattleMap): GameState = {
    val units = map.points.map(_._2).toTraversable.select[CombatUnitObject].map(_.unit)
    val eq = units.foldLeft(new EventQueue[Event]) {
      (agg, next) => agg.repeat(UnitTurn(next)).infinite.inIntervalsOf(1.0)
    }
    GameState(map, eq)
  }
  def unitSetter(original: CombatUnit): Setter[GameState, CombatUnit] =
    Setter[GameState, CombatUnit](replacer => gs => gs.mapUnit(original, replacer))
}
