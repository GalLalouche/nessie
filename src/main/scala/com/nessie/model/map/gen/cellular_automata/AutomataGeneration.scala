package com.nessie.model.map.gen.cellular_automata

import com.nessie.model.map.{BattleMap, BattleMapObject, FullWall}

sealed trait AutomataGeneration extends BattleMapObject
object AutomataGeneration {
  def canonize(map: BattleMap): BattleMap =
    map.foldPoints((map, next) => map(next) match {
      case Empty(_) | CaveMapObject(_) | Tunnel => map.remove(next)
      case Wall(_) => map.place(next, FullWall)
    })
}
case class Empty(index: Int) extends AutomataGeneration
case class Wall(index: Int) extends AutomataGeneration {
  override val obstructsVision = true
  override val canMoveThrough = false
}
object Tunnel extends AutomataGeneration
case class CaveMapObject(identifier: Char) extends AutomataGeneration
