package com.nessie.gm

import com.nessie.model.map._
import com.nessie.model.units._
import com.nessie.view.sfx.ScalaFxViewFactory

private object GameLoop {
  def createInitialState: GameState = {
    val map = DictBattleMap(10, 10)
        .place(MapPoint(0, 0), CombatUnitObject(new Warrior))
        .place(MapPoint(0, 1), CombatUnitObject(new Archer))
        .place(DirectionalMapPoint(0, 0, Direction.Down), Wall)
        .place(MapPoint(9, 8), CombatUnitObject(new Skeleton))
        .place(MapPoint (9, 9), CombatUnitObject(new Zombie))
    GameState fromMap map
  }

  def main(args: Array[String]): Unit = {
    val guiFactory: ViewFactory = ScalaFxViewFactory
    val view = guiFactory.create()
    val gameMaster: Iterator[GameState] = GameMaster.initiate(createInitialState, view.playerInput)
    while (true) {
      val nextState = gameMaster.next()
      view.updateState(nextState)
    }
  }
}
