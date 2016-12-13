package com.nessie.map.view.sfx

import javafx.embed.swing.JFXPanel
import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{scene => jfxs}

import com.nessie.gm.{GameState, View, ViewFactory}
import com.nessie.map.model.{BattleMap, MapPoint}
import com.nessie.map.{BattleMapObject, CombatUnitObject, EmptyMapObject}
import com.nessie.units.{CombatUnit, Skeleton, Warrior}
import common.rich.RichT._

import scala.concurrent.Future
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.control.{Button, Label}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.GridPane
import scalafx.scene.{Node, Scene}
import scalafx.stage.Stage

object ScalaFxViewFactory extends ViewFactory {
  new JFXPanel
  Platform.implicitExit = false
  override def create(): View = new ScalaFxView()
}
