package com.nessie.view.zirconview

import org.hexworks.zircon.api.component.Component

private class InstructionsPanel private(panel: TextBoxPanel) {
  def update(i: Instructions): Unit = panel.update(
    _.addHeader("Movement")
        .addListItem("Use the WASD keys to move the selected unit; press space to confirm movement")
        .addListItem("Use the mouse to perform actions like attack")
        .addListItem("Press enter to end turn if you have movement squares remaining")
  )
  def clear(): Unit = panel.clear()
  def component: Component = panel.component
}

private object InstructionsPanel {
  def create(placer: PanelPlacer): InstructionsPanel = {
    val panel = TextBoxPanel.apply("Instructions", placer)
    new InstructionsPanel(panel)
  }
}
