package com.nessie.gm

import com.nessie.model.map.MapPoint

sealed trait GameStateChange

case object NoOp extends GameStateChange
case class Movement(src: MapPoint, dst: MapPoint) extends GameStateChange
case class Attack(src: MapPoint, dst: MapPoint, damageAmount: Int) extends GameStateChange
