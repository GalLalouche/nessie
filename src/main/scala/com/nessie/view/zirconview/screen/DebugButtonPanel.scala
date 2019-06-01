package com.nessie.view.zirconview.screen

import com.nessie.gm.DebugMapStepper
import com.nessie.model.map.BattleMap
import com.nessie.model.map.fov.FogOfWar
import com.nessie.view.zirconview.{ComponentWrapper, OnBuildWrapper, PanelPlacer, ZirconConstants}
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.screen.DebugButtonPanel.StepperWrapper
import common.rich.primitives.RichBoolean._
import common.rich.RichT._
import common.rich.func.ToMoreFoldableOps
import org.hexworks.zircon.api.{Components, Positions}
import org.hexworks.zircon.api.component.{Button, CheckBox, Component, Panel}
import rx.lang.scala.{Observable, Subject}

import scala.collection.JavaConverters._

import scalaz.std.OptionInstances

private class DebugButtonPanel private(
    stepperWrapper: StepperWrapper,
    panel: Panel,
    smallStepButton: Button,
    bigStepButton: Button,
) extends ComponentWrapper {
  override def component: Component = panel
  smallStepButton.onActivation(() => nextSmallStep())
  def nextSmallStep(): Unit = {
    stepperWrapper.nextSmallStep()
    smallStepButton.getDisabledProperty.setValue(stepperWrapper.hasNextSmallStep().isFalse)
  }
  bigStepButton.onActivation(() => nextBigStep())
  def nextBigStep(): Unit = {
    stepperWrapper.nextBigStep()
    bigStepButton.getDisabledProperty.setValue(stepperWrapper.hasNextBigStep().isFalse)
    smallStepButton.getDisabledProperty.setValue(stepperWrapper.hasNextSmallStep().isFalse)
  }
  val hoverFov: CheckBox = panel.getChildren.iterator.asScala
      .flatMap(_.safeCast[CheckBox])
      .find(_.getText == "Hover FOV")
      .get
  def isHoverFovChecked: Boolean = hoverFov.isChecked
  def mapObservable: Observable[FogOfWar] = stepperWrapper.observable
}

private object DebugButtonPanel
    extends ToMoreFoldableOps with OptionInstances {
  private[this] def buildPanel(pp: PanelPlacer, bps: OnBuildWrapper[_ <: Component, _]*): Panel =
    Components.panel
        .withTitle("Debug")
        .wrapWithBox(true)
        .|>(pp)
        .<|(_.addComponents(bps, Positions.zero, Positions.create(-1, 0)))

  private class StepperWrapper(private var stepper: DebugMapStepper) {
    def hasNextSmallStep(): Boolean = stepper.hasNextSmallStep()
    private val $ = Subject[FogOfWar]()
    private def update(bm: BattleMap): Unit = $.onNext(FogOfWar.allVisible(bm))
    def nextSmallStep(): Unit = {
      stepper = stepper.nextSmallStep().get
      update(stepper.currentMap)
    }
    def finishCurrentStep(): Unit = {
      stepper = stepper.finishCurrentStep()
      update(stepper.currentMap)
    }

    def hasNextBigStep(): Boolean = stepper.hasNextBigStep()
    def nextBigStep(): Unit = {
      stepper = stepper.nextBigStep().get
      update(stepper.currentMap)
    }
    def canonize(): Unit = {
      update(stepper.canonize)
    }

    def observable: Observable[FogOfWar] = $
  }

  def create(stepper: DebugMapStepper, panelPlacer: PanelPlacer): DebugButtonPanel = {
    val wrapper = new StepperWrapper(stepper)
    val panel = buildPanel(
      panelPlacer,
      OnBuildWrapper.noOp(Components.button.withText("Small Step")),
      OnBuildWrapper(Components.button.withText("Finish Step"))(
        _.onActivation(() => wrapper.finishCurrentStep())),
      OnBuildWrapper.noOp(Components.button.withText("Big Step")),
      OnBuildWrapper(Components.button.withText("Canonize"))(
        _.onActivation(() => wrapper.canonize())),
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
