package com.nessie.model.map

trait GridFactory {
  def apply[A](gs: GridSize, initialObject: A): Grid[A]
  def apply[A](rows: Seq[Seq[A]]): Grid[A]
}
