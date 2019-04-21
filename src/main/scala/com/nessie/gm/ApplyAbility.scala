package com.nessie.gm

import com.nessie.model.map.CombatUnitObject
import monocle.syntax.ApplySyntax

/** Changes the GameState by applying a GameStateChange. Doesn't verify the validity of the change. */
// TODO verify the validity of the change?
private object ApplyAbility extends ApplySyntax {
  def apply(gameStateChange: GameStateChange)(gs: GameState): GameState = gameStateChange match {
    case Movement(src, dst, _) =>
      gs.&|->(GameState.map).modify(_ move src to dst)
    case Attack(_, dst, damage, _) =>
      val unit = gs.map(dst).asInstanceOf[CombatUnitObject].unit
      GameState.unitSetter(unit).^|->(unit.hitPointsLens).modify(_.reduceHp(damage))(gs)
  }
}
