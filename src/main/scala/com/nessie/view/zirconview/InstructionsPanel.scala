package com.nessie.view.zirconview

import org.hexworks.zircon.api.component.Component

private class InstructionsPanel private(panel: TextBoxPanel) {
  private var stack: List[Instructions] = List[Instructions]()
  def update(i: Instructions): Unit = i match {
    case Instructions.Movement => panel.update(
      _.addHeader("Movement")
          .addListItem("Use the WASD keys to move the selected unit; press space to confirm movement")
          .addListItem("Use the mouse to perform actions like attack")
          .addListItem("Press enter to end turn if you have movement squares remaining")
    )
  }
  def push(i: Instructions): Unit = synchronized {
    stack = i :: stack
    update(stack.head)
  }
  def popInstructions(): Unit = synchronized {
    if (stack.nonEmpty) stack = stack.tail
    if (stack.nonEmpty) update(stack.head) else clear()
  }
  def clear(): Unit = panel.clear()
  def component: Component = panel.component
}

private object InstructionsPanel {
  def create(placer: PanelPlacer): InstructionsPanel = {
    val panel = TextBoxPanel.apply("Instructions", placer)
    new InstructionsPanel(panel)
  }
}
