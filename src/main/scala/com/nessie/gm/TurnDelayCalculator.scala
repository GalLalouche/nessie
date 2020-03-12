package com.nessie.gm

private object TurnDelayCalculator {
  private val BaseDelay = 0.5
  def apply(ct: ComposedTurn): Double = {
    val movementDelay = 1 - ct.remainingMovement.toDouble / ct.unit.moveAbility.range
    assert(movementDelay >= 0)
    val actionDelay = if (ct.isInstanceOf[PostAction]) 1.0 else 0.0
    assert(actionDelay >= 0)
    BaseDelay + movementDelay + actionDelay
  }
}
