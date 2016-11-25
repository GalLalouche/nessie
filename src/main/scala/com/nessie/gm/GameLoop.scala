package com.nessie.gm

import com.nessie.map.CombatUnitObject
import com.nessie.map.model.{ArrayBattleMap, MapPoint}
import com.nessie.map.view.string.StringViewFactory
import com.nessie.units.{Skeleton, Warrior}

object GameLoop {
  lazy val guiFactory: ViewFactory = StringViewFactory

  def createInitialState: GameState = {
    val map = ArrayBattleMap(5, 5)
        .place(MapPoint(0, 0), CombatUnitObject(new Warrior))
        .place(MapPoint(4, 4), CombatUnitObject(new Skeleton))
    GameState fromMap map
  }

  val gameMaster: Iterator[GameState] = GameMaster.initiate(createInitialState)
  def main(args: Array[String]): Unit = {
    while (true) {
      val nextState = gameMaster.next()
      val nextView = guiFactory(nextState)
      nextView.display()
    }
  }
}
