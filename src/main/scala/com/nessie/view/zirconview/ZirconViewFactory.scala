package com.nessie.view.zirconview

import com.google.inject.Inject
import com.nessie.gm.{GameState, IterativeViewFactory, NoOp, View}
import common.rich.RichT._

class ZirconViewFactory extends IterativeViewFactory {
  private var zirconViewCustomizer = ZirconViewCustomizer.Null
  @Inject(optional = true)
  private def setApiKey(c: ZirconViewCustomizer): Unit = {
    this.zirconViewCustomizer = c
  }
  override def create(states: Iterator[GameState]): View =
    new ZirconView(zirconViewCustomizer, states) <| (_.updateState(NoOp, states.next()))
  override def create(): View = create(Iterator.empty)
}
