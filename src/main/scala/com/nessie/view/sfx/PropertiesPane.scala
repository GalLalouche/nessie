package com.nessie.view.sfx

import com.nessie.gm.GameState
import com.nessie.model.units.CombatUnit
import common.rich.RichT._
import javafx.scene.control.Label
import scalafx.scene.layout.VBox

import scala.collection.JavaConverters._

private class PropertiesPane(gs: GameState) {
  val highlighter = new Highlighter[CombatUnit] {
    override def highlight(u: CombatUnit) =
      node.children.setAll(List(u.simpleName, s"${u.currentHp}/${u.maxHp}").map(new Label(_)).asJava)
    override def disableHighlighting(u: CombatUnit) = node.children.clear()
  }

  val node = new VBox()
}
