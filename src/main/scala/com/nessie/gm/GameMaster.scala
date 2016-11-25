package com.nessie.gm

import com.nessie.map.model.BattleMap
import com.nessie.units.CombatUnit
import com.nessie.units.Owner._

/** A GameMaster runs the game. It keeps the current state of the game, and can fetch the next state */
private object GameMaster {
  private val ai = new CatcherAI

  def getPlayerMove(u: CombatUnit)(map: BattleMap): BattleMap = {
    scala.io.StdIn.readLine("Placeholder, player isn't moving. Press enter to continue.")
    map
  }

  private def nextState(gs: GameState): GameState = {
    val eq = gs.eq
    val nextEvent: Event = eq.head
    val nextMap: BattleMap => BattleMap = nextEvent match {
      case UnitTurn(u) => u.owner match {
        case AI => ai(u)
        case Player => getPlayerMove(u)
      }
    }
    GameState.map.modify(nextMap).andThen(GameState.eq.modify(_.tail))(gs)
  }
  def initiate(state: GameState): Iterator[GameState] = Stream.iterate(state)(nextState).toIterator
}
