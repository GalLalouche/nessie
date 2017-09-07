package com.nessie.view.sfx

import javafx.scene.control.Label

import com.nessie.gm.GameState
import com.nessie.model.map._
import com.nessie.model.units.CombatUnit
import common.rich.RichT._

import scala.collection.JavaConversions._
import scalafx.scene.layout.VBox

private class PropertiesPane(gs: GameState) extends NodeWrapper with Highlighter[CombatUnit] {
  override def highlight(u: CombatUnit) =
    node.children.setAll(List(u.simpleName, s"${u.currentHp }/${u.maxHp }").map(new Label(_)))
  override def disableHighlighting(u: CombatUnit) = node.children.clear()

  val node = new VBox()
}
