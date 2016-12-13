package com.nessie.gm

import com.nessie.map.CombatUnitObject
import com.nessie.map.model.{ArrayBattleMap, MapPoint}
import com.nessie.map.view.sfx.ScalaFxViewFactory
import com.nessie.units.{Skeleton, Warrior}

private object GameLoop {
  def createInitialState: GameState = {
    val map = ArrayBattleMap(5, 5)
        .place(MapPoint(0, 0), CombatUnitObject(new Warrior))
        .place(MapPoint(4, 4), CombatUnitObject(new Skeleton))
    GameState fromMap map
  }

  def main(args: Array[String]): Unit = {
    val guiFactory: ViewFactory = ScalaFxViewFactory
    val view = guiFactory.create()
    val gameMaster: Iterator[GameState] = GameMaster.initiate(createInitialState, view)
    while (true) {
      val nextState = gameMaster.next()
      view.updateState(nextState)
    }
  }
}
