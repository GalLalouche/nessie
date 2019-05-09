package com.nessie.view.zirconview

import com.nessie.gm.{DebugMapStepper, GameState, GameStateChange, NoOp, PlayerInput, View}
import com.nessie.model.units.CombatUnit
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import org.hexworks.zircon.api.{AppConfigs, Components, CP437TilesetResources, Positions, Screens, Sizes, SwingApplications}
import org.hexworks.zircon.api.component.{CheckBox, ComponentAlignment}

import scala.collection.JavaConverters._

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

  private var map: Option[ZirconMap] = None
  private val mapViewPosition = Positions.create(0, 1).relativeToRightOf(propertiesPanel.component)
  private def drawMap(): Unit = screen.draw(map.get.graphics, mapViewPosition)
  override def updateState(change: GameStateChange, state: GameState): Unit = map match {
    case None =>
      val newMap = ZirconMap.create(state.map, customizer.mapCustomizer)
      map = Some(newMap)
      drawMap()
      // TODO convert InputEmitter to ObservableFactory
      screen.onMouseMoved(me => {
        val gc = me.getPosition.withInverseRelative(mapViewPosition).toMapPoint(newMap.map)
        propertiesPanel.update(newMap.map)(gc)
        gc.filter(isHoverFovChecked.const) |> newMap.drawFov
        drawMap()
      })
    case Some(value) =>
      value.update(state.map)
      drawMap()
  }
  val panel = DebugButtonBuilder(
    _.withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT),
    OnBuildWrapper(Components.button.withText("Small Step"))(_.onMouseClicked(_ => nextSmallStep())),
    OnBuildWrapper(Components.button.withText("Big Step"))(_.onMouseClicked(_ => nextBigStep())),
    OnBuildWrapper.noOp(Components.checkBox.withText("Hover FOV")),
  )
  private def isHoverFovChecked: Boolean = panel.getChildren.iterator.asScala
      .flatMap(_.safeCast[CheckBox])
      .find(_.getText == "Hover FOV")
      .get
      .isChecked
  screen.addComponent(panel)

  screen.display()

  override def playerInput = new PlayerInput {
    override def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState) = ???
  }

  def nextSmallStep(): Unit = {
    stepper = stepper.nextSmallStep().get
    updateState(NoOp, GameState.fromMap(stepper.currentMap))
  }

  private def nextBigStep(): Unit = {
    stepper = stepper.nextBigStep().get
    updateState(NoOp, GameState.fromMap(stepper.currentMap))
  }
}
