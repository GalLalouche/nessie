package com.nessie.view.zirconview

import ch.qos.logback.classic.Level
import com.nessie.gm.{DebugMapStepper, GameState, GameStateChange, PlayerInput, View}
import com.nessie.model.units.CombatUnit
import com.nessie.view.zirconview.input.ZirconPlayerInput
import org.hexworks.zircon.api.{AppConfigs, CP437TilesetResources, Positions, Screens, Sizes, SwingApplications}
import org.hexworks.zircon.api.component.ComponentAlignment
import org.slf4j.LoggerFactory

private class ZirconView(customizer: ZirconViewCustomizer, private var stepper: DebugMapStepper) extends View {
  LoggerFactory.getLogger("org.hexworks").asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.WARN)

  private val tileGrid = SwingApplications.startTileGrid(
    AppConfigs.newConfig()
        .withSize(Sizes.create(100, 80))
        .withDefaultTileset(CP437TilesetResources.wanderlust16x16())
        .build())

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
  private val mapGridPosition = Positions.create(0, 1).relativeToRightOf(propertiesPanel.component)
  override def updateState(change: GameStateChange, state: GameState): Unit = {
    def drawMap(): Unit = screen.draw(map.get.graphics, mapGridPosition)
    def createNewMap(state: GameState): ZirconMap = {
      val $ = ZirconMap.create(state.map, customizer.mapCustomizer, mapGridPosition)
      $.mouseEvents(screen).foreach(gc => {
        propertiesPanel.update($.getCurrentBattleMap)(gc)
        if (debugPanel.isHoverFovChecked) {
          $.drawFov(gc)
          drawMap()
        }
      })
      $
    }
    map match {
      case None => map = Some(createNewMap(state))
      case Some(value) => value.update(state.map)
    }
    drawMap()
  }

  private def drawMap(): Unit = screen.draw(map.get.graphics, mapGridPosition)

  screen.display()

  override def playerInput = new PlayerInput {
    override def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState) =
      new ZirconPlayerInput(
        screen,
        map.get,
        instructions,
        screenDrawer = drawMap,
      ).nextState(currentlyPlayingUnit, gs)
  }

  def nextSmallStep(): Unit = debugPanel.nextSmallStep()
}
