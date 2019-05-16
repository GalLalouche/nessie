package com.nessie.model.map

trait BattleMapObject {
  def obstructsVision: Boolean = false
  def canMoveThrough: Boolean = true
}
