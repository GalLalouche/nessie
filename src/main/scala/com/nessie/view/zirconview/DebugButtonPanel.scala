package com.nessie.view.zirconview

import com.nessie.gm.{DebugMapStepper, GameState, View}
import common.rich.primitives.RichBoolean._
import com.nessie.gm.GameStateChange.NoOp
import com.nessie.view.zirconview.DebugButtonPanel.StepperWrapper
import com.nessie.view.zirconview.ZirconUtils._
import common.rich.RichT._
import common.rich.func.ToMoreFoldableOps
import org.hexworks.zircon.api.{Components, Positions}
import org.hexworks.zircon.api.component.{Button, CheckBox, Component, Panel}

import scala.collection.JavaConverters._

import scalaz.std.OptionInstances

private class DebugButtonPanel private(
    stepperWrapper: StepperWrapper,
    panel: Panel,
    smallStepButton: Button,
    bigStepButton: Button,
) {
  def component: Component = panel
  smallStepButton.onActivation(() => nextSmallStep())
  def nextSmallStep(): Unit = {
    if (smallStepButton.isEnabled.isFalse) // Because disable buttons can be still be clicked on for some reason!
      return
    stepperWrapper.nextSmallStep()
    smallStepButton.getDisabledProperty.setValue(stepperWrapper.hasNextSmallStep.isFalse)
  }
  bigStepButton.onActivation(() => nextBigStep())
  def nextBigStep(): Unit = {
    if (bigStepButton.isEnabled.isFalse)
      return
    stepperWrapper.nextBigStep()
    bigStepButton.getDisabledProperty.setValue(stepperWrapper.hasNextBigStep.isFalse)
    smallStepButton.getDisabledProperty.setValue(stepperWrapper.hasNextSmallStep.isFalse)
  }
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
        .|>(pp)
        .<|(_.addComponents(bps, Positions.zero, Positions.create(-1, 0)))

  private class StepperWrapper(private var stepper: DebugMapStepper, view: View) {
    def hasNextSmallStep: Boolean = stepper.nextSmallStep().isDefined
    def nextSmallStep(): Unit = {
      stepper = stepper.nextSmallStep().get
      view.updateState(NoOp, GameState.fromMap(stepper.currentMap))
    }

    def hasNextBigStep: Boolean = stepper.nextBigStep().isDefined
    def nextBigStep(): Unit = {
      stepper = stepper.nextBigStep().get
      view.updateState(NoOp, GameState.fromMap(stepper.currentMap))
    }
  }

  def create(stepper: DebugMapStepper, view: View, panelPlacer: PanelPlacer): DebugButtonPanel = {
    val wrapper = new StepperWrapper(stepper, view)
    val panel = buildPanel(
      panelPlacer,
      OnBuildWrapper.noOp(Components.button.withText("Small Step")),
      OnBuildWrapper.noOp(Components.button.withText("Big Step")),
      OnBuildWrapper.noOp(Components.checkBox.withText("Hover FOV")),
    )
    panel.applyColorTheme(ZirconConstants.Theme)
    new DebugButtonPanel(wrapper, panel,
      panel.collect {
        case b: Button if b.getText == "Small Step" => b
      }.next,
      panel.collect {
        case b: Button if b.getText == "Big Step" => b
      }.next,
    )
  }
}
