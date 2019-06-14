package com.nessie.gm

import com.nessie.model.eq.EventQueue
import com.nessie.model.map.BattleMap
import com.nessie.model.units.CombatUnit

import monocle.Setter
import monocle.macros.Lenses

@Lenses
case class GameState(
    mapAndFogs: MapAndFogs,
    eq: EventQueue[Event],
    currentTurn: Option[ComposedTurn], // Is defined when it's time for a unit to take its turn.
) {
  val map: BattleMap = mapAndFogs.map

  private def mapUnit(original: CombatUnit, replacer: CombatUnit => CombatUnit): GameState = {
    val mapper = UnitMapper(original, replacer)
    GameState.mapAndFogs.modify(mapper.mapAndFogs)
        .andThen(GameState.eq.modify(mapper.eq))
        .apply(this)
  }
}
object GameState {
  def fromMap(map: BattleMap): GameState = {
    val mapf = MapAndFogs.teamFov(map)
    val units = mapf.units
    val eq = units.foldLeft(new EventQueue[Event]) {
      (agg, next) => agg.add(UnitTurn(next), 1.0 / next.moveAbility.range)
    }
    GameState(mapf, eq, currentTurn = None)
  }
  def unitSetter(original: CombatUnit): Setter[GameState, CombatUnit] =
    Setter[GameState, CombatUnit](replacer => _.mapUnit(original, replacer))
}
