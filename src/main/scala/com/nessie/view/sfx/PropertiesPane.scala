package com.nessie.view.sfx

import com.nessie.gm.GameState
import com.nessie.model.units.{CombatUnit, PlayerUnit}
import com.nessie.model.units.stats.Stats
import common.rich.RichT._
import javafx.scene.control.Label
import scalafx.scene.layout.VBox

import scala.collection.JavaConverters._

private class PropertiesPane(gs: GameState) {
  val highlighter: Focuser[CombatUnit] = new Focuser[CombatUnit] {
    private def parseStats(stats: Stats): Seq[String] = Vector(
      "Stats:",
      "  Strength: " + stats.strength,
      "  Constitution: " + stats.constitution,
      "  Dexterity: " + stats.dexterity,
    )
    override def focus(u: CombatUnit) = {
      val strings = List(
        u.metadata.name,
        s"${u.hitPoints.currentHp}/${u.hitPoints.maxHp}",
      ) ++ u.safeCast[PlayerUnit].map(_.stats).toIterator.flatMap(parseStats)
      node.children.setAll(strings.map(new Label(_)).asJava)
    }
    override def unfocus(u: CombatUnit) = node.children.clear()
  }

  val node = new VBox()
}
