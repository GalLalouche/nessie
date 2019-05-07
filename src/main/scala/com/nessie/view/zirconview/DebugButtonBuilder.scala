package com.nessie.view.zirconview

import common.rich.RichT._
import org.hexworks.zircon.api.{Components, Positions, Sizes}
import org.hexworks.zircon.api.component.{Button, Component, Panel}

private object DebugButtonBuilder {
  type ButtonProperties = (String, () => Any)
  private def aux(above: Component, bps: List[ButtonProperties]): List[Button] = bps match {
    case Nil => Nil
    case (name, action) :: tail =>
      val next = Components.button()
          .withText(name)
          .withPosition(Positions.zero().relativeToBottomOf(above))
          .build()
      next.onMouseClicked(_ => action())
      next :: aux(next, tail)
  }
  def apply(p: Placer, bps: ButtonProperties*): Panel = {
    val $ = Components.panel
        .withTitle("Debug")
        .withSize(Sizes.create(20, 80))
        .wrapWithBox(true)
        .<|(p)
        .build
    // FIXME hack for placing buttons correctly
    val header = Components.header().withText("Not displayed").build()
    aux(header, bps.toList).foreach($.addComponent)
    $
  }
}
