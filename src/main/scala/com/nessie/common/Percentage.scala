package com.nessie.common

import scala.language.implicitConversions

//TODO move to ScalaCommon
case class Percentage private(p: Double) extends AnyVal

object Percentage {
  def apply(d: Double): Percentage = {
    require(d >= 0 && d <= 1)
    new Percentage(d)
  }
  /** Ensures perimeter check. */
  implicit def doubleToPercentage(d: Double): Percentage = apply(d)

  implicit object OrderingEv extends Ordering[Percentage] {
    override def compare(x: Percentage, y: Percentage): Int = x.p.compare(y.p)
  }
}
