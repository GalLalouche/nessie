package com.nessie.common

// TODO move to ScalaCommon
object RichIterable {
  def from[A](i: => Iterator[A]): Iterable[A] = new Iterable[A] {
    override def iterator = i
  }
}
