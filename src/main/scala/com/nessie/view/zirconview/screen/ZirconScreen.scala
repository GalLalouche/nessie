package com.nessie.view.zirconview.screen

import com.nessie.gm.DebugMapStepper
import com.nessie.model.map.fov.FogOfWar
import com.nessie.view.zirconview.{InstructionsPanel, MapPointHighlighter, ZirconViewCustomizer}
import com.nessie.view.zirconview.map.ZirconMap
import org.hexworks.zircon.api.screen.Screen

private[zirconview] trait ZirconScreen {
  def screen: Screen
  val highlighter: MapPointHighlighter
  def instructions: InstructionsPanel
  def map: ZirconMap
  def updateMap(fow: FogOfWar): Unit
  def drawMap(): Unit
  def nextSmallStep(): Unit
}

private[zirconview] object ZirconScreen {
  def create(customizer: ZirconViewCustomizer, stepper: DebugMapStepper): ZirconScreen =
    ZirconScreenImplFactory(customizer, stepper)
}
