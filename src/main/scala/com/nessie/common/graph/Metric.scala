package com.nessie.common.graph

trait Metric[A] {
  def distance(a1: A, a2: A): Double
}

object Metric {
  object Implicits {
    implicit class ToMetricOps[A: Metric]($: A) {
      def distanceTo(other: A): Double = implicitly[Metric[A]].distance($, other)
    }
  }
}
