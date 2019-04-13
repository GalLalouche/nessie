package com.nessie.gm

import com.nessie.model.map._
import com.nessie.model.units._
import com.nessie.view.sfx.ScalaFxViewFactory

private object GameLoop {
  def createInitialState: GameState = {
    val map = DictBattleMap(5, 5)
        .place(MapPoint(0, 0), CombatUnitObject(Warrior.create))
        .place(MapPoint(0, 1), CombatUnitObject(Archer.create))
        .place(DirectionalMapPoint(0, 0, Direction.Down), Wall)
        .place(MapPoint(4, 4), CombatUnitObject(Skeleton.create))
        .place(MapPoint(3, 4), CombatUnitObject(Zombie.create))
    GameState fromMap map
  }

  def main(args: Array[String]): Unit = {
    val guiFactory: ViewFactory = ScalaFxViewFactory
    val view = guiFactory.create()
    val gameMaster: Iterator[(GameStateChange, GameState)] =
      GameMaster.initiate(createInitialState, view.playerInput)
    while (true)
      (view.updateState _).tupled(gameMaster.next())

  }
}
