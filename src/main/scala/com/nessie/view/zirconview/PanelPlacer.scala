package com.nessie.view.zirconview

import org.hexworks.zircon.api.builder.component.PanelBuilder
import org.hexworks.zircon.api.component.{ComponentAlignment, Panel}
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.screen.Screen

private sealed trait PanelPlacer extends (PanelBuilder => Panel)

private object PanelPlacer {
  def sizeAndAlignment(width: Int, height: Int, screen: Screen, ca: ComponentAlignment): PanelPlacer =
    sizeAndAlignment(Sizes.create(width, height), screen, ca)
  def sizeAndAlignment(size: Size, screen: Screen, ca: ComponentAlignment) = new PanelPlacer {
    override def apply(pb: PanelBuilder) = pb.withSize(size).withAlignmentWithin(screen, ca).build
  }
}

