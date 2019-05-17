package com.nessie.view.zirconview

import com.nessie.gm.{DebugMapStepper, GameState, View}
import com.nessie.gm.GameStateChange.NoOp
import com.nessie.view.zirconview.DebugButtonPanel.StepperWrapper
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import common.rich.func.ToMoreFoldableOps
import org.hexworks.zircon.api.{Components, Positions}
import org.hexworks.zircon.api.component.{CheckBox, Component, Panel}

import scala.collection.JavaConverters._

import scalaz.std.OptionInstances

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

private object DebugButtonPanel
    extends ToMoreFoldableOps with OptionInstances {
  private[this] def buildPanel(pp: PanelPlacer, bps: OnBuildWrapper[_ <: Component, _]*): Panel =
    Components.panel
        .withTitle("Debug")
        .wrapWithBox(true)
        .<|(pp)
        .build
        .<|(_.addComponents(bps, Positions.zero, Positions.create(-1, 0)))

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
      OnBuildWrapper(Components.button.withText("Small Step"))(_.onActivation(() => wrapper.nextSmallStep())),
      OnBuildWrapper(Components.button.withText("Big Step"))(_.onActivation(() => wrapper.nextBigStep())),
      OnBuildWrapper.noOp(Components.checkBox.withText("Hover FOV")),
    )
    panel.applyColorTheme(ZirconConstants.Theme)
    new DebugButtonPanel(wrapper, panel)
  }
}
