package com.nessie.view.zirconview

import com.nessie.gm.{GameState, GameStateChange, NoOp, View}
import com.nessie.model.map.MapPoint
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import org.hexworks.zircon.api.{AppConfigs, Components, CP437TilesetResources, Positions, Screens, Sizes, SwingApplications}
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.input.MouseAction

private class ZirconView(customizer: ZirconViewCustomizer, iterator: Iterator[GameState]) extends View {
  private val tileGrid = SwingApplications.startTileGrid(
    AppConfigs.newConfig()
        .withSize(Sizes.create(100, 80))
        .withDefaultTileset(CP437TilesetResources.wanderlust16x16())
        .build())

  private val screen = Screens.createScreenFor(tileGrid)
  private val propertiesPane = PropertiesPane.create(_
      .withSize(20, 80)
      .withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT)
  )

  override def updateState(change: GameStateChange, state: GameState): Unit = {
    val mapView = ZirconMap.createGraphics(state.map, customizer.mapCustomizer)

    val mapViewPosition = Positions.create(0, 1).relativeToRightOf(propertiesPane.component)
    screen.draw(mapView, mapViewPosition)
    def toGridCoordinates(me: MouseAction): Option[MapPoint] =
      me.getPosition.withInverseRelative(mapViewPosition).toMapPoint(state.map)
    screen.onMouseMoved(toGridCoordinates(_) |> propertiesPane.update)
  }
  private val nextButton =
    Components.button().withText("next").withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT).build()
  screen.addComponent(propertiesPane.component)
  nextButton.onMouseClicked(_ => updateState(NoOp, iterator.next()))

  screen.addComponent(nextButton)
  screen.display()

  override def playerInput = ???
}
