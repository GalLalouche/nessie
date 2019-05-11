package com.nessie.view.zirconview

import com.nessie.gm.{DebugMapStepper, GameState, View}
import com.nessie.gm.GameStateChange.NoOp
import com.nessie.view.zirconview.DebugButtonPanel.StepperWrapper
import common.rich.collections.RichIterator._
import common.rich.RichT._
import org.hexworks.zircon.api.{Components, Positions}
import org.hexworks.zircon.api.component.{CheckBox, Component, Panel}

import scala.collection.JavaConverters._

private class DebugButtonPanel private(stepperWrapper: StepperWrapper, panel: Panel) {
  def component: Component = panel
  def nextSmallStep(): Unit = stepperWrapper.nextSmallStep()
  def nextBigStep(): Unit = stepperWrapper.nextBigStep()
  def isHoverFovChecked: Boolean = panel.getChildren.iterator.asScala
      .flatMap(_.safeCast[CheckBox])
      .find(_.getText == "Hover FOV")
      .get
      .isChecked
}

private object DebugButtonPanel {
  private[this] def buildPanel(pp: PanelPlacer, bps: OnBuildWrapper[_ <: Component, _]*): Panel = {
    val $ = Components.panel
        .withTitle("Debug")
        .wrapWithBox(true)
        .<|(pp)
        .build
    bps.foreach {obBuildWrapper =>
      val children = $.getChildren.iterator.asScala
      val position =
      // TODO RichIterator.lastOption
        if (children.isEmpty) Positions.zero else Positions.create(-1, 0).relativeToBottomOf(children.last)
      obBuildWrapper.cb.withPosition(position)
      $.addComponent(obBuildWrapper.build())
    }
    $
  }
  private class StepperWrapper(private var stepper: DebugMapStepper, view: View) {
    def nextSmallStep(): Unit = {
      stepper = stepper.nextSmallStep().get
      view.updateState(NoOp, GameState.fromMap(stepper.currentMap))
    }

    def nextBigStep(): Unit = {
      stepper = stepper.nextBigStep().get
      view.updateState(NoOp, GameState.fromMap(stepper.currentMap))
    }
  }

  def create(stepper: DebugMapStepper, view: View, panelPlacer: PanelPlacer): DebugButtonPanel = {
    val wrapper = new StepperWrapper(stepper, view)
    val panel = buildPanel(
      panelPlacer,
      OnBuildWrapper(Components.button.withText("Small Step"))(_.onMouseClicked(_ => wrapper.nextSmallStep())),
      OnBuildWrapper(Components.button.withText("Big Step"))(_.onMouseClicked(_ => wrapper.nextBigStep())),
      OnBuildWrapper.noOp(Components.checkBox.withText("Hover FOV")),
    )
    panel.applyColorTheme(ZirconConstants.Theme)
    new DebugButtonPanel(wrapper, panel)
  }
}
