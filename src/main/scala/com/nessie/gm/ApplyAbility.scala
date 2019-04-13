package com.nessie.gm

import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.CombatUnit
import monocle.syntax.ApplySyntax

import scalaz.Writer
import scalaz.std.VectorInstances

private object ApplyAbility extends ApplySyntax
    with VectorInstances {
  private def getUnit(map: BattleMap, point: MapPoint): CombatUnit =
    map(point).asInstanceOf[CombatUnitObject].unit
  type LoggedAction = Writer[Vector[String], GameState]
  def apply(gameStateChange: GameStateChange)(gs: GameState): GameState = gameStateChange match {
    case Movement(src, dst) => gs.&|->(GameState.map).modify(_ move src to dst)
    case Attack(_, dst, damage) =>
      val map = gs.map
      val unit = getUnit(map, dst)
      GameState.unitSetter(unit).^|->(unit.hitPointsLens).modify(_.reduceHp(damage))(gs)
  }
}
