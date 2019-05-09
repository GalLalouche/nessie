package com.nessie.view.zirconview

import com.nessie.gm.{DebugMapStepper, GameState, GameStateChange, PlayerInput, View}
import com.nessie.model.units.CombatUnit
import org.hexworks.zircon.api.{AppConfigs, CP437TilesetResources, Positions, Screens, Sizes, SwingApplications}
import org.hexworks.zircon.api.component.ComponentAlignment

private class ZirconView(customizer: ZirconViewCustomizer, private var stepper: DebugMapStepper) extends View {
  private val tileGrid = SwingApplications.startTileGrid(
    AppConfigs.newConfig()
        .withSize(Sizes.create(100, 80))
        .withDefaultTileset(CP437TilesetResources.wanderlust16x16())
        .build())

  private val screen = Screens.createScreenFor(tileGrid)
  private val propertiesPanel =
    PropertiesPanel.create(PanelPlacer.sizeAndAlignment(20, 80, screen, ComponentAlignment.TOP_LEFT))
  screen.addComponent(propertiesPanel.component)

  private val debugPanel: DebugButtonPanel =
    DebugButtonPanel.create(stepper, this,
      PanelPlacer.sizeAndAlignment(20, 80, screen, ComponentAlignment.TOP_RIGHT))
  screen.addComponent(debugPanel.component)
  screen.display()

  private var map: Option[ZirconMap] = None
  override def updateState(change: GameStateChange, state: GameState): Unit = {
    val mapViewPosition = Positions.create(0, 1).relativeToRightOf(propertiesPanel.component)
    def drawMap(): Unit = screen.draw(map.get.graphics, mapViewPosition)
    def createNewMap(state: GameState): ZirconMap = {
      val $ = ZirconMap.create(state.map, customizer.mapCustomizer)
      $.mouseEvents(screen, mapViewPosition).foreach(gc => {
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

  override def playerInput = new PlayerInput {
    override def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState) = ???
  }

  def nextSmallStep(): Unit = debugPanel.nextSmallStep()
}
