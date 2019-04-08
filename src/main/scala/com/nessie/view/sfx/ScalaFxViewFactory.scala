package com.nessie.view.sfx

import javafx.embed.swing.JFXPanel
import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{scene => jfxs}

import com.nessie.gm.{View, ViewFactory}

import scalafx.application.Platform

object ScalaFxViewFactory extends ViewFactory {
  new JFXPanel // Initialize Toolkit.
  Platform.implicitExit = false
  override def create(): View = new ScalaFxView()
}
