package com.nessie.view.zirconview.screen

import ch.qos.logback.classic.Level
import com.nessie.gm.DebugMapStepper
import com.nessie.view.zirconview.{ComponentWrapper, InstructionsPanel, PanelPlacer, ZirconViewCustomizer}
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.map.ZirconMap
import common.rich.RichT._
import org.hexworks.zircon.api.{AppConfigs, CP437TilesetResources, Positions, Screens, Sizes, SwingApplications}
import org.hexworks.zircon.api.component.ComponentAlignment
import org.slf4j.LoggerFactory

private object ZirconScreenImplFactory {
  def apply(customizer: ZirconViewCustomizer, stepper: DebugMapStepper): ZirconScreenImpl = {
    LoggerFactory.getLogger("org.hexworks").asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.WARN)
    val tileGrid = SwingApplications.startTileGrid(
      AppConfigs.newConfig
          .withSize(Sizes.create(100, 80))
          .withDefaultTileset(CP437TilesetResources.wanderlust16x16)
          .build)
    val screen = Screens.createScreenFor(tileGrid)
    def add[A <: ComponentWrapper](w: Int, h: Int, ca: ComponentAlignment)(builder: PanelPlacer => A): A =
      builder(PanelPlacer.sizeAndAlignment(w, h, screen, ca)).<|(screen addComponent _.component)

    val properties = add(20, 80, ComponentAlignment.TOP_LEFT)(PropertiesPanel.create)

    val instructions = add(
      screen.getWidth - properties.component.getWidth, 10, ComponentAlignment.BOTTOM_RIGHT
    )(InstructionsPanel.create)

    val debugPanel = add(20, 50, ComponentAlignment.TOP_RIGHT)(DebugButtonPanel.create(stepper, _))

    screen.display()

    val mapGridPosition = Positions.create(0, 1).relativeToRightOf(properties.component)
    val map: ZirconMap = ZirconMap.create(customizer.mapCustomizer, mapGridPosition, Sizes.create(50, 50))
    screen.display()

    val $ = new ZirconScreenImpl(screen, properties, debugPanel, instructions, mapGridPosition, map)
    map.mouseEvents(screen)
        // This can fail if the event occurs before the map was updated
        .filter(_.exists(map.getCurrentMap.isInBounds))
        .foreach(mp => {
          properties.update(map.getCurrentMap.map)(mp)
          if (debugPanel.isHoverFovChecked) {
            map.updateViewAndFog(mp)
            $.drawMap()
          }
        })
    debugPanel.hoverFov.onActivation(() => if (debugPanel.isHoverFovChecked) map.showAll() else map.hideAll())
    debugPanel.mapObservable.foreach($.updateMap)
    $
  }
}
