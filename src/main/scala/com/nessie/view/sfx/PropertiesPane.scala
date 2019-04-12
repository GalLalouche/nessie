package com.nessie.view.sfx

import com.nessie.gm.GameState
import com.nessie.model.units.CombatUnit
import javafx.scene.control.Label
import scalafx.scene.layout.VBox

import scala.collection.JavaConverters._

private class PropertiesPane(gs: GameState) {
  val highlighter = new Highlighter[CombatUnit] {
    override def highlight(u: CombatUnit) =
      node.children.setAll(List(u.metadata.name, s"${u.hitPoints.currentHp}/${u.hitPoints.maxHp}").map(new Label(_)).asJava)
    override def disableHighlighting(u: CombatUnit) = node.children.clear()
  }

  val node = new VBox()
}
