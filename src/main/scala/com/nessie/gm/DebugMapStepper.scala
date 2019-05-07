package com.nessie.gm

import com.nessie.model.map.BattleMap

trait DebugMapStepper {
  def currentMap: BattleMap
  def nextSmallStep(): Option[DebugMapStepper]
  def nextBigStep(): Option[DebugMapStepper]
}

object DebugMapStepper {
  def Null: DebugMapStepper = new DebugMapStepper {
    override def currentMap = ???
    override def nextSmallStep() = None
    override def nextBigStep() = None
  }
}
