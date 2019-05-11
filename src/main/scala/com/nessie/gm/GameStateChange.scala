package com.nessie.gm

sealed trait GameStateChange

object GameStateChange {
  case object NoOp extends GameStateChange
  case class ActionTaken(a: TurnAction) extends GameStateChange
}
