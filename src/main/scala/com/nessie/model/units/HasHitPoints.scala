package com.nessie.model.units

import monocle.Lens

trait HasHitPoints { type T <: HasHitPoints
  val currentHp: Int
  val maxHp: Int

  protected def withHp(amount: Int): T

  def isDead = currentHp == 0
  def reduceHp(amount: Int): T = withHp(Math.max(0, currentHp - amount))
  def healHp(amount: Int): T = withHp(Math.min(maxHp, currentHp + amount))
}

object HasHitPoints {
  val currentHp = Lens[HasHitPoints, Int](_.currentHp)(hp => hhp => hhp withHp hp)
}