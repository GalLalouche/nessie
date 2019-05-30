package com.nessie.view.zirconview.screen

import com.nessie.model.map.fov.FogOfWar
import com.nessie.view.zirconview.{InstructionsPanel, MapPointHighlighter}
import com.nessie.view.zirconview.map.ZirconMap
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.screen.Screen

private class ZirconScreenImpl(
    override val screen: Screen,
    propertiesPanel: PropertiesPanel,
    debugPanel: DebugButtonPanel,
    override val instructions: InstructionsPanel,
    val mapGridPosition: Position,
    override val map: ZirconMap,
) extends ZirconScreen {
  override val highlighter: MapPointHighlighter = propertiesPanel.highlighter
  override def updateMap(fow: FogOfWar): Unit = synchronized {
    map.update(fow)
    drawMap()
  }
  override def drawMap(): Unit = synchronized {
    screen.draw(map.graphics, mapGridPosition)
    val layer = map.fogOfWarLayer
    screen.removeLayer(layer)
    screen.pushLayer(layer)
  }
  override def nextSmallStep(): Unit = debugPanel.nextSmallStep()
  screen.display()
  map.mouseEvents(screen)
      // This can fail if the event occurs before the map was updated
      .filter(_.exists(map.getCurrentMap.isInBounds))
      .foreach(mp => {
        propertiesPanel.update(map.getCurrentMap.map)(mp)
        if (debugPanel.isHoverFovChecked) {
          map.updateViewAndFog(mp)
          drawMap()
        }
      })
}
