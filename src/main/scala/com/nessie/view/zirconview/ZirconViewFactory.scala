package com.nessie.view.zirconview

import com.nessie.gm.{GameState, NoOp, View, ViewFactory}
import common.rich.RichT._

object ZirconViewFactory extends ViewFactory {
  def createWithIterator(c: ZirconViewCustomizer, states: Iterator[GameState]): View =
    new ZirconView(c, states) <| (_.updateState(NoOp, states.next()))
  override def create(): View = createWithIterator(ZirconViewCustomizer.Null, Iterator.empty)
}
