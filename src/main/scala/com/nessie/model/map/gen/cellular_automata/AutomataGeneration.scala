package com.nessie.model.map.gen.cellular_automata

import com.nessie.model.map.{BattleMap, BattleMapObject, EmptyMapObject, FullWall}

private sealed trait AutomataGeneration extends BattleMapObject
private object AutomataGeneration {
  def canonize(map: BattleMap): BattleMap = map.map {
    case Empty(_) | CaveMapObject(_) | Tunnel => EmptyMapObject
    case Wall(_) => FullWall
    case e => e
  }
}
private case class Empty(index: Int) extends AutomataGeneration
private case class Wall(index: Int) extends AutomataGeneration {
  override val obstructsVision = true
  override val canMoveThrough = false
}
private object Tunnel extends AutomataGeneration
private case class CaveMapObject(identifier: Char) extends AutomataGeneration
