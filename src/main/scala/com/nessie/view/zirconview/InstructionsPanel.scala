package com.nessie.view.zirconview

import org.hexworks.zircon.api.component.Component

private class InstructionsPanel private(panel: TextBoxPanel) {
  private var stack: List[Instructions] = Nil
  def update(i: Instructions): Unit = synchronized {
    i match {
      case Instructions.BasicInput => panel.update(
        _.addHeader("Input")
            .addListItem("Use the WASD/HJKL keys to select target cell")
            .addListItem("press space to popup a menu at location")
            .addListItem("press 'M' to to move to location")
            .addListItem("Press enter to end turn")
            .addListItem("Use the arrow keys to scroll the map; shift/ctrl scrolls farther")
      )
    }
  }
  def push(i: Instructions): Unit = synchronized {
    stack = i :: stack
    update(stack.head)
  }
  def popInstructions(): Unit = synchronized {
    if (stack.nonEmpty) stack = stack.tail
    if (stack.nonEmpty) update(stack.head) else clear()
  }
  def clear(): Unit = synchronized {panel.clear()}
  def component: Component = panel.component
}

private object InstructionsPanel {
  def create(placer: PanelPlacer): InstructionsPanel = {
    val panel = TextBoxPanel.apply("Instructions", placer)
    new InstructionsPanel(panel)
  }
}
