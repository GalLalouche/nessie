package com.nessie.gm

import com.nessie.common.graph.GridDijkstra
import com.nessie.common.graph.GridDijkstra.Blockable
import com.nessie.gm.GameStateChange.ActionTaken
import com.nessie.gm.TurnAction.{ActualAction, MovementAction}
import com.nessie.model.map.{BattleMapObject, CombatUnitObject}
import com.nessie.model.map.fov.TeamFov
import common.rich.primitives.RichBoolean._
import common.rich.RichT._
import monocle.syntax.ApplySyntax

/** Changes the GameState by applying a GameStateChange. Doesn't verify the validity of the change. */
// TODO verify the validity of the change?
private object ApplyAbility extends ApplySyntax {
  private implicit val blockableEv: Blockable[BattleMapObject] = Blockable(_.canMoveThrough.isFalse)
  def apply(gameStateChange: GameStateChange)(gs: GameState): GameState = gameStateChange match {
    case ActionTaken(a) =>
      val currentTurn = gs.currentTurn.get
      a match {
        case MovementAction(m@Movement(src, dst)) =>
          // TODO avoid calculating distance twice
          val distance =
            GridDijkstra(gs.map.grid, m.src, maxDistance = m.manhattanDistance).apply(m.dst).ceil.toInt
          require(gs.map.isEmptyAt(dst))
          require(gs.map.isOccupiedAt(src))
          val o = gs.map(src)
          val movedMap = gs.&|->(GameState.map)
              .modify(_.remove(src) |> (_.place(dst, o)))
          movedMap.&|->(GameState.fogOfWar)
              .modify(_.updateVisible(
                // TODO replace with injection
                TeamFov.visibleForOwner(movedMap.map(dst).asInstanceOf[CombatUnitObject], movedMap.map))
              ).copy(currentTurn = Some(currentTurn + MovementWithDistance(m, distance)))
        case ActualAction(a@Attack(_, dst, damage, _)) =>
          val unit = gs.map(dst).asInstanceOf[CombatUnitObject].unit
          GameState.unitSetter(unit).^|->(unit.hitPointsLens).modify(_.reduceHp(damage))(gs)
              .copy(currentTurn = Some(currentTurn.asInstanceOf[PreAction] + a))
      }
  }
}
