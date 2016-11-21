package com.nessie.ai

import com.nessie.map.model.{BattleMap, BattleMapController, MapPoint}
import com.nessie.map.objects.CombatUnitObject

// no pun intended
class CatcherAI {
  def apply(map: BattleMap, p: MapPoint): BattleMapController => Unit = {
    val warriorP = map.find(e => e._1 != p && e._2.isInstanceOf[CombatUnitObject]).get._1
    val destination = map.toStream
        .map(_._1)
        .filter(_.manhattenDistanceTo(p) <= 2)
        .filter(_ != warriorP)
        .minBy(_ manhattenDistanceTo warriorP)
    _ move p to destination
  }
}
