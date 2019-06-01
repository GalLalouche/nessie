package com.nessie.model.map.gen.cellular_automata

import com.nessie.model.map.{BattleMap, BattleMapObject, EmptyMapObject, FullWall}

sealed trait AutomataGeneration extends BattleMapObject
object AutomataGeneration {
  def canonize(map: BattleMap): BattleMap = map.map {
    case Empty(_) | CaveMapObject(_) | Tunnel => EmptyMapObject
    case Wall(_) => FullWall
    case e => e
  }
}
case class Empty(index: Int) extends AutomataGeneration
case class Wall(index: Int) extends AutomataGeneration {
  override val obstructsVision = true
  override val canMoveThrough = false
}
object Tunnel extends AutomataGeneration
case class CaveMapObject(identifier: Char) extends AutomataGeneration
