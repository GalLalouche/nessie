package com.nessie.view.zirconview

import common.rich.RichT._
import org.hexworks.zircon.api.builder.component.TextBoxBuilder
import org.hexworks.zircon.api.component.{Component, Panel}
import org.hexworks.zircon.api.Components

private class TextBoxPanel private(panel: Panel) {
  def update(tbb: TextBoxBuilder => Any): Unit = {
    panel.clear()
    panel.addComponent(Components.textBox.withContentWidth(panel.getWidth - 3).<|(tbb).build)
    panel.applyColorTheme(ZirconConstants.Theme)
  }
  def clear(): Unit = panel.clear()
  def component: Component = panel
}

private object TextBoxPanel {
  def apply(title: String, panelPlacer: PanelPlacer) = new TextBoxPanel(
    Components
        .panel()
        .withTitle(title)
        .wrapWithBox(true)
        .|>(panelPlacer)
        .<|(_.applyColorTheme(ZirconConstants.Theme))
  )
}
