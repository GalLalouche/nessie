package com.nessie.common.graph

/**
 * Metric laws: https://en.wikipedia.org/wiki/Metric_(mathematics)#Definition
 * - Metric.distance(x, y) >= 0
 * - Metric.distance(x, y) == 0 iff x == y
 * - Metric(x, y) == Metric(y, x)
 * - Metric(x, y) + Metric(y, z) >= Metric(x, z)
 */
trait Metric[A] {
  def distance(a1: A, a2: A): Double
}

object Metric {
  object Implicits {
    implicit class ToMetricOps[A: Metric]($: A) {
      def distanceTo(other: A): Double = implicitly[Metric[A]].distance($, other)
    }
  }

  implicit val intMetric: Metric[Int] = (x, y) => math.abs(x - y)
  implicit val longMetric: Metric[Long] = (x, y) => math.abs(x - y)
  implicit val doubleMetric: Metric[Double] = (x, y) => math.abs(x - y)
}
