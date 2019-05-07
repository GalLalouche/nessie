package com.nessie.view.zirconview

import com.nessie.gm.{DebugMapStepper, GameState, GameStateChange, NoOp, PlayerInput, View}
import com.nessie.model.map.MapPoint
import com.nessie.model.units.CombatUnit
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import org.hexworks.zircon.api.{AppConfigs, CP437TilesetResources, Positions, Screens, Sizes, SwingApplications}
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.input.MouseAction

private class ZirconView(customizer: ZirconViewCustomizer, private var stepper: DebugMapStepper) extends View {
  private var hoverFov: Boolean = false
  private val tileGrid = SwingApplications.startTileGrid(
    AppConfigs.newConfig()
        .withSize(Sizes.create(100, 80))
        .withDefaultTileset(CP437TilesetResources.wanderlust16x16())
        .build())

  private val screen = Screens.createScreenFor(tileGrid)
  private val propertiesPane = PropertiesPanel.create(_
      .withSize(20, 80)
      .withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT)
  )
  screen.addComponent(propertiesPane.component)

  override def updateState(change: GameStateChange, state: GameState): Unit = {
    val mapView = ZirconMap.createGraphics(state.map, customizer.mapCustomizer)

    val mapViewPosition = Positions.create(0, 1).relativeToRightOf(propertiesPane.component)
    screen.draw(mapView, mapViewPosition)
    def toGridCoordinates(me: MouseAction): Option[MapPoint] =
      me.getPosition.withInverseRelative(mapViewPosition).toMapPoint(state.map)
    screen.onMouseMoved(toGridCoordinates(_) |> propertiesPane.update(state.map))
  }
  val panel = DebugButtonBuilder(
    _.withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT),
    "Small Step" -> (() => nextSmallStep()),
    "Large Step" -> (() => nextLargeStep()),
  )
  screen.addComponent(panel)

  screen.display()

  override def playerInput = new PlayerInput {
    override def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState) = ???
  }

  def nextSmallStep(): Unit = {
    stepper = stepper.nextSmallStep().get
    updateState(NoOp, GameState.fromMap(stepper.currentMap))
  }

  private def nextLargeStep(): Unit = {
    stepper = stepper.nextBigStep().get
    updateState(NoOp, GameState.fromMap(stepper.currentMap))
  }
}
