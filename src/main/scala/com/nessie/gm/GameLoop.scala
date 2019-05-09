package com.nessie.gm

import com.google.inject.Guice
import com.nessie.model.map._
import com.nessie.model.units._
import com.nessie.view.ViewModule
import net.codingwell.scalaguice.InjectorExtensions._

/**
 * The game loop is the main application entry point. It runs a while(true) loop (hence the name), and fetches
 * the next state from the game master.
 */
object GameLoop {
  private def DemoState = GameState.fromMap(
    DictBattleMap(5, 5)
        .place(MapPoint(0, 0), CombatUnitObject(Warrior.create))
        .place(MapPoint(0, 1), CombatUnitObject(Archer.create))
        .place(MapPoint(1, 0), FullWall)
        .place(MapPoint(4, 4), CombatUnitObject(Skeleton.create))
        .place(MapPoint(3, 4), CombatUnitObject(Zombie.create))
  )

  def main(args: Array[String]): Unit = {
    val view = Guice.createInjector(ViewModule).instance[ViewFactory].create()
    initialize(view, DemoState)
  }

  // TODO make stoppbable
  def initialize(view: View, currentState: GameState): Unit = {
    val iterator = GameMaster.initiate(currentState, view.playerInput)
    while (true)
      (view.updateState _).tupled(iterator.next())
  }
}
