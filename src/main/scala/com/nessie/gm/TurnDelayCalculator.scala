package com.nessie.gm

import common.rich.RichT._

private object TurnDelayCalculator {
  private val BaseDelay = 0.5
  def apply(ct: ComposedTurn): Double = {
    val movementDelay = 1 - ct.remainingMovement.toDouble / ct.unit.moveAbility.range
    assert(movementDelay >= 0)
    val actionDelay = 1.0.onlyIf(ct.isInstanceOf[PostAction])
    assert(actionDelay >= 0)
    BaseDelay + movementDelay + actionDelay
  }
}
