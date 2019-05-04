package com.nessie.gm

trait IterativeViewFactory extends ViewFactory {
  def create(i: Iterator[GameState]): View
}
