package com.nessie.view.sfx

import com.nessie.gm.{GameState, View, ViewFactory}
import com.nessie.gm.GameStateChange.NoOp
import common.rich.RichT._
import javafx.embed.swing.JFXPanel
import scalafx.application.Platform

object ScalaFxViewFactory extends ViewFactory {
  new JFXPanel // Initialize Toolkit.
  Platform.implicitExit = false
  override def create(): View = new ScalaFxView(customizer = ScalaFxViewCustomizer.none)
  def create(customizer: ScalaFxViewCustomizer): View = new ScalaFxView(customizer)
  def createWithIterator(customizer: ScalaFxViewCustomizer, i: Iterator[GameState]): View =
    new ScalaFxView(customizer, i) <| (_.updateState(NoOp, i.next()))
}
