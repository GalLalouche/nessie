package com.nessie.gm

import com.nessie.model.map.MapPoint

sealed trait GameStateChange

case object NoOp extends GameStateChange

trait UnitAction extends GameStateChange {
  def turnDelay: Double
}
case class Movement(src: MapPoint, dst: MapPoint, override val turnDelay: Double) extends UnitAction
case class Attack(
    src: MapPoint, dst: MapPoint, damageAmount: Int, override val turnDelay: Double) extends UnitAction
