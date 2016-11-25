package com.nessie.gm

import com.nessie.events.model.EventQueue
import com.nessie.map.CombatUnitObject
import com.nessie.map.model.BattleMap
import common.rich.func.MoreMonadPlus._
import common.rich.func.RichMonadPlus._
import monocle.macros.Lenses

@Lenses
case class GameState(map: BattleMap, eq: EventQueue[Event])
object GameState {
  def fromMap(map: BattleMap): GameState = {
    val units = map.points.map(_._2).toTraversable.select[CombatUnitObject].map(_.unit)
    val eq = units.foldLeft(new EventQueue[Event]) {
      (agg, next) => agg.repeat(UnitTurn(next)).infinite.inIntervalsOf(1.0)
    }
    GameState(map, eq)
  }
}
