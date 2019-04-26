package com.nessie.view.zirconview

import com.nessie.model.map.MapPoint
import common.rich.RichT._
import org.hexworks.zircon.api.{Components, Positions}
import org.hexworks.zircon.api.builder.component.PanelBuilder
import org.hexworks.zircon.api.component.{Component, Panel}

private class PropertiesPane private(panel: Panel) {
  panel.applyColorTheme(ZirconConstants.Theme)
  def update(mp: Option[MapPoint]): Unit = {
    panel.clear()
    mp.foreach(mp => panel.addComponent(
      Components.textBox()
          .withContentWidth(panel.getWidth - 2)
          .addParagraph(mp.toString)
          .withPosition(Positions.zero().relativeToTopOf(panel))
          .build()
    ))
    panel.applyColorTheme(ZirconConstants.Theme)
  }
  def component: Component = panel
}

private object PropertiesPane {
  def create(panelBuilder: PanelBuilder => Any): PropertiesPane = new PropertiesPane(Components
      .panel()
      .withTitle("Properties pane")
      .wrapWithBox(true)
      .<|(panelBuilder)
      .build()
  )
}
