package com.nessie.view.zirconview.screen

import com.nessie.gm.DebugMapStepper
import com.nessie.model.map.fov.FogOfWar
import com.nessie.view.zirconview.{InstructionsPanel, MapPointHighlighter, ModalResultWrapper, ZirconViewCustomizer}
import com.nessie.view.zirconview.map.ZirconMap
import org.hexworks.zircon.api.component.modal.Modal
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.UIEventSource
import rx.lang.scala.Observable

import scalaz.concurrent.Task

private[zirconview] trait ZirconScreen extends UIEventSource {
  def screen: Screen
  val highlighter: MapPointHighlighter
  def instructions: InstructionsPanel
  def map: ZirconMap
  def updateMap(fow: FogOfWar): Unit
  def drawMap(): Unit
  def nextSmallStep(): Unit

  def modalTask[A](m: Modal[ModalResultWrapper[A]]): Task[A]
}

private[zirconview] object ZirconScreen {
  def create(customizer: ZirconViewCustomizer, stepper: DebugMapStepper): ZirconScreen =
    ZirconScreenImplFactory(customizer, stepper)
}
