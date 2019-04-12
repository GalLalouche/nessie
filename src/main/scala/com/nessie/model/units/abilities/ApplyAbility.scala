package com.nessie.model.units.abilities

import com.nessie.gm.GameState
import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.CombatUnit
import monocle.syntax.ApplySyntax

object ApplyAbility extends ApplySyntax {
  private def getUnit(map: BattleMap, point: MapPoint): CombatUnit =
    map(point).asInstanceOf[CombatUnitObject].unit
  def apply(ability: UnitAbility)(src: MapPoint, dst: MapPoint)(gs: GameState): GameState = {
    val map = gs.map
    assert(CanBeUsed(ability)(map, src, dst))
    ability match {
      case MoveAbility(_) => gs.&|->(GameState.map).modify(_ move src to dst)
      case d: DamageAbility =>
        val damage = d.damage
        val unit = getUnit(map, dst)
        GameState.unitSetter(unit).^|->(unit.hitPointsLens).modify(_.reduceHp(damage))(gs)
    }
  }
}
