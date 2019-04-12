package com.nessie.view.string

import com.nessie.gm.{GameState, PlayerInput, View}
import com.nessie.model.map.{BattleMapObject, CombatUnitObject, EmptyMapObject, MapPoint}
import com.nessie.model.units.{CombatUnit, Skeleton, Warrior}

import scala.concurrent.Future

private class StringBattleMapViewer extends View {
  private def print(o: BattleMapObject): Char = o match {
    case EmptyMapObject => '_'
    case CombatUnitObject(u) => u.metadata.name.head
  }
  override def updateState(state: GameState): Unit = {
    val m = state.map
    val rows = 0 until m.height map (y => 0 until m.width map (x => m(MapPoint(x, y))))
    rows.map(_.map(print).mkString(",")).mkString("\n")
  }
  val playerInput = new PlayerInput {
    override def nextState(u: CombatUnit)(gs: GameState): Future[GameState] = {
      println("StringViewer doesn't support input from player")
      Future successful gs
    }
  }
}
