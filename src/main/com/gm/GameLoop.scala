package com.nessie.gm

import com.nessie.map.model.ArrayBattleMap
import com.nessie.map.objects.CombatUnitObject
import com.nessie.units.{Skeleton, Warrior}

object GameLoop {
  val initialMap = ArrayBattleMap(5, 5)
      .updated((1, 2), CombatUnitObject(new Warrior))
      .updated((3, 1), CombatUnitObject(new Skeleton))
  val gameState: GameState = GameState.fromMap(initialMap)
  val viewFactory: ViewFactory = ???
  val gameMaster: Iterator[GameState] = GameMaster(gameState)
  var gui = ???
  while (true) {
    val nextState = gameMaster.next()
    val nextView = viewFactory(nextState)
    nextView.displayAndWaitForCompletion()
  }
}
