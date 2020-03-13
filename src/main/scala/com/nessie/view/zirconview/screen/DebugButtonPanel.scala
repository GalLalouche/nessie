package com.nessie.view.zirconview.screen

import com.nessie.gm.DebugMapStepper
import com.nessie.model.map.BattleMap
import com.nessie.view.zirconview.{ComponentWrapper, OnBuildWrapper, PanelPlacer, ZirconConstants}
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.screen.DebugButtonPanel.StepperWrapper
import org.hexworks.zircon.api.{Components, Positions}
import org.hexworks.zircon.api.component.{Button, CheckBox, Component, Panel}
import rx.lang.scala.{Observable, Subject}

import scala.collection.JavaConverters._

import common.rich.primitives.RichBoolean._
import common.rich.RichT._

private class DebugButtonPanel private(
    stepperWrapper: StepperWrapper,
    panel: Panel,
    smallStepButton: Button,
    bigStepButton: Button,
    finishAllButton: Button,
) extends ComponentWrapper {
  import common.rich.func.MoreSeqInstances._
  import common.rich.func.ToMoreMonadPlusOps._

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
  finishAllButton.onActivation(() => {
    panel.children.select[Button].foreach(_.disable())
    stepperWrapper.finishAll()
    debugButtonsSubject.onNext(DebugButton.FinishAll(stepperWrapper.currentMap))
  })
  val hoverFov: CheckBox = panel.getChildren.iterator.asScala
      .flatMap(_.safeCast[CheckBox])
      .find(_.getText == "Hover FOV")
      .get
  def isHoverFovChecked: Boolean = hoverFov.isChecked
  def mapObservable: Observable[BattleMap] = stepperWrapper.observable
  private val debugButtonsSubject = Subject[DebugButton]()
  def debugButtons: Observable[DebugButton] = debugButtonsSubject
}

private object DebugButtonPanel {
  private[this] def buildPanel(pp: PanelPlacer, bps: OnBuildWrapper[_ <: Component, _]*): Panel =
    Components.panel
        .withTitle("Debug")
        .wrapWithBox(true)
        .|>(pp)
        .<|(_.addComponents(bps, Positions.zero, Positions.create(-1, 0)))

  private class StepperWrapper(private var stepper: DebugMapStepper) {
    def currentMap = stepper.currentMap

    def hasNextSmallStep(): Boolean = stepper.hasNextSmallStep()
    private val $ = Subject[BattleMap]()
    private def update(bm: BattleMap): Unit = $.onNext(bm)
    private def update(f: DebugMapStepper => DebugMapStepper): Unit = {
      stepper = f(stepper)
      update(stepper.currentMap)
    }
    def nextSmallStep(): Unit = update(_.nextSmallStep().get)
    def finishCurrentStep(): Unit = update(_.finishCurrentStep())

    def hasNextBigStep(): Boolean = stepper.hasNextBigStep()
    def nextBigStep(): Unit = update(_.nextBigStep().get)
    def finishAll(): Unit = {
      stepper = stepper.finishAll()
      update(stepper.canonize)
    }
    def canonize(): Unit = update(stepper.canonize)

    def observable: Observable[BattleMap] = $
  }

  def create(stepper: DebugMapStepper, panelPlacer: PanelPlacer): DebugButtonPanel = {
    val wrapper = new StepperWrapper(stepper)
    val BigStep = "Big Step"
    val SmallStep = "Small Step"
    val FinishAll = "Finish All"
    val panel = buildPanel(
      panelPlacer,
      OnBuildWrapper.noOp(Components.button.withText(SmallStep)),
      OnBuildWrapper(Components.button.withText("Finish Step"))(
        _.onActivation(() => wrapper.finishCurrentStep())),
      OnBuildWrapper.noOp(Components.button.withText(BigStep)),
      OnBuildWrapper.noOp(Components.button.withText(FinishAll)),
      OnBuildWrapper(Components.button.withText("Canonize"))(
        _.onActivation(() => wrapper.canonize())),
      OnBuildWrapper.noOp(Components.checkBox.withText("Hover FOV")),
    )
    panel.applyColorTheme(ZirconConstants.Theme)
    def findButton(text: String): Button = panel.collect {
      case b: Button if b.getText == text => b
    }.next
    new DebugButtonPanel(wrapper, panel,
      smallStepButton = findButton(SmallStep),
      bigStepButton = findButton(BigStep),
      finishAllButton = findButton(FinishAll),
    )
  }
}
