package com.nessie.gm

import com.nessie.model.map.BattleMap

trait DebugMapStepper {
  def currentMap: BattleMap
  def nextSmallStep(): Option[DebugMapStepper]
  def nextBigStep(): Option[DebugMapStepper]
  /** Remove any internal map objects and replace them with the canonical ones, e.g., FullWall and EmptyMapObject. */
  def canonize: BattleMap
}

object DebugMapStepper {
  def Null: DebugMapStepper = new DebugMapStepper {
    override def currentMap = ???
    override def nextSmallStep() = None
    override def nextBigStep() = None
    override def canonize = ???
  }
}
