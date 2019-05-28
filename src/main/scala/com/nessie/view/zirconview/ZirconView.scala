package com.nessie.view.zirconview

import ch.qos.logback.classic.Level
import com.nessie.gm.{DebugMapStepper, GameState, GameStateChange, PlayerInput, View}
import com.nessie.model.map.Direction
import com.nessie.model.units.CombatUnit
import com.nessie.view.zirconview.input.ZirconPlayerInput
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.map.ZirconMap
import org.hexworks.zircon.api.{AppConfigs, CP437TilesetResources, Positions, Screens, Sizes, SwingApplications}
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.uievent.KeyCode
import org.slf4j.LoggerFactory

private class ZirconView(customizer: ZirconViewCustomizer, private var stepper: DebugMapStepper) extends View {
  LoggerFactory.getLogger("org.hexworks").asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.WARN)

  private val tileGrid = SwingApplications.startTileGrid(
    AppConfigs.newConfig
        .withSize(Sizes.create(100, 80))
        .withDefaultTileset(CP437TilesetResources.wanderlust16x16)
        .build)

  private val screen = Screens.createScreenFor(tileGrid)
  private val propertiesPanel =
    PropertiesPanel.create(PanelPlacer.sizeAndAlignment(20, 80, screen, ComponentAlignment.TOP_LEFT))
  screen.addComponent(propertiesPanel.component)

  private val instructions = InstructionsPanel.create(PanelPlacer.sizeAndAlignment(
    screen.getWidth - propertiesPanel.component.getWidth, 10, screen, ComponentAlignment.BOTTOM_RIGHT)
  )
  screen.addComponent(instructions.component)

  private val debugPanel: DebugButtonPanel =
    DebugButtonPanel.create(stepper, this,
      PanelPlacer.sizeAndAlignment(20, 50, screen, ComponentAlignment.TOP_RIGHT))
  screen.addComponent(debugPanel.component)

  screen.display()

  private var map: Option[ZirconMap] = None
  debugPanel.hoverFov.onActivation(() => {
    if (debugPanel.isHoverFovChecked) map.get.showAll() else map.get.hideAll()
  })
  private val mapGridPosition = Positions.create(0, 1).relativeToRightOf(propertiesPanel.component)
  override def updateState(change: GameStateChange, state: GameState): Unit = synchronized {
    def drawMap(): Unit = {
      screen.draw(map.get.graphics, mapGridPosition)
      val layer = map.get.fogOfWarLayer
      screen.removeLayer(layer)
      screen.pushLayer(layer)
    }
    def createNewMap(state: GameState): ZirconMap = {
      val $ = ZirconMap.create(state.fogOfWar, customizer.mapCustomizer, mapGridPosition, Sizes.create(50, 50))
      $.mouseEvents(screen).foreach(gc => {
        propertiesPanel.update($.getCurrentMap.map)(gc)
        if (debugPanel.isHoverFovChecked) {
          $.updateViewAndFog(gc)
          drawMap()
        }
      })
      $
    }
    map match {
      case None => map = Some(createNewMap(state))
      case Some(value) => value.update(state.fogOfWar)
    }
    drawMap()
  }

  private def drawMap(): Unit = screen.draw(map.get.graphics, mapGridPosition)

  screen.display()

  private val ArrowKeys: Map[KeyCode, Direction] = Map(
    KeyCode.UP -> Direction.Up,
    KeyCode.DOWN -> Direction.Down,
    KeyCode.LEFT -> Direction.Left,
    KeyCode.RIGHT -> Direction.Right,
  )
  screen.keyboardActions().oMap(ke => ArrowKeys.get(ke.getCode).strengthL(ke)).foreach {case (ke, d) =>
    map.get.scroll(n = if (ke.getCtrlDown) 10 else if (ke.getShiftDown) 5 else 1, direction = d)
    drawMap()
  }
  override def playerInput = new PlayerInput {
    override def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState) =
      new ZirconPlayerInput(
        screen,
        map.get,
        instructions,
        screenDrawer = drawMap,
        highlighter = propertiesPanel.highlighter,
      ).nextState(currentlyPlayingUnit, gs)
  }

  def nextSmallStep(): Unit = debugPanel.nextSmallStep()
}
