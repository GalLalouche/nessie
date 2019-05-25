package com.nessie.model.map

trait GridFactory {
  def apply[A](width: Int, height: Int, initialObject: A): Grid[A]
}
