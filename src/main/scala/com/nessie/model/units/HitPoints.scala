package com.nessie.model.units

import monocle.macros.Lenses

@Lenses
case class HitPoints(currentHp: Int, maxHp: Int) {
  require(currentHp >= 0)
  require(currentHp <= maxHp)
  def isDead: Boolean = currentHp == 0
  def isNotDead: Boolean = currentHp != 0
  def reduceHp(amount: Int): HitPoints = {
    require(amount >= 0)
    copy(currentHp = Math.max(currentHp - amount, 0))
  }
  def healHp(amount: Int): HitPoints = {
    require(amount >= 0)
    copy(currentHp = Math.min(currentHp + amount, maxHp))
  }
}
object HitPoints {
  def fullHp(maxHp: Int) = HitPoints(currentHp = maxHp, maxHp = maxHp)
}
