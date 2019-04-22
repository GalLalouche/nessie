package com.nessie.view.sfx

import com.nessie.gm.{View, ViewFactory}
import javafx.embed.swing.JFXPanel
import scalafx.application.Platform

object ScalaFxViewFactory extends ViewFactory {
  new JFXPanel // Initialize Toolkit.
  Platform.implicitExit = false
  override def create(): View = new ScalaFxView(customizer = ScalaFxViewCustomizer.none)
  def create(customizer: ScalaFxViewCustomizer): View = new ScalaFxView(customizer)
}
