package com.nessie.model.map.gen.cellular_automata

import com.nessie.model.map.BattleMapObject

sealed trait AutomataGeneration extends BattleMapObject{
  def n: Int
}
private case class Empty(override val n: Int) extends AutomataGeneration
private case class Wall(override val n: Int) extends AutomataGeneration {
  override val obstructsVision = true
  override val canMoveThrough = false
}
