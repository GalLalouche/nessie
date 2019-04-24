package com.nessie.view.sfx

import com.nessie.gm.GameState
import com.nessie.model.map.{CombatUnitObject, MapPoint}
import com.nessie.model.units.{CombatUnit, PlayerUnit}
import com.nessie.model.units.inventory.Equipment
import com.nessie.model.units.stats.Stats
import common.rich.RichT._
import common.rich.func.ToMoreFoldableOps
import javafx.scene.control.Label
import scalafx.scene.layout.VBox

import scala.collection.JavaConverters._

import scalaz.std.OptionInstances

private class PropertiesPane(gs: GameState)
    extends ToMoreFoldableOps with OptionInstances {
  val node = new VBox()

  val pointHighlighter: Focuser[MapPoint] = new Focuser[MapPoint] {
    override def focus(mp: MapPoint) = {
      val optionalUnitLines =
        gs.map(mp).safeCast[CombatUnitObject].mapHeadOrElse(PropertiesPane combatUnitLines _.unit, Nil)
      showLines(mp.toString +: optionalUnitLines, force = true)
    }
    override def unfocus(mp: MapPoint) = PropertiesPane.this.unfocus()
  }

  // E.g., from the event queue bar above
  val combatUnitHighlighter: Focuser[CombatUnit] = new Focuser[CombatUnit] {
    override def focus(u: CombatUnit) = showLines(PropertiesPane.combatUnitLines(u), force = false)
    override def unfocus(u: CombatUnit) = PropertiesPane.this.unfocus()
  }

  // The below hacks force the pointHighlighter to always take precedence over the combatUnit one, since it
  // includes the same information as well the point it self. We do that by ignoring showLines invocation when
  // there is a already a focus on something and force is false.
  private var noFocus: Boolean = true
  private def showLines(lines: Seq[String], force: Boolean): Unit = this.synchronized {
    if (noFocus || force)
      node.children.setAll(lines.map(new Label(_)).asJava)
    noFocus = false
  }
  private def unfocus(): Unit = this.synchronized {
    node.children.clear()
    noFocus = true
  }
}

private object PropertiesPane {
  private[this] def parseStats(stats: Stats): Seq[String] = Vector(
    "Stats:",
    "  Strength: " + stats.strength,
    "  Constitution: " + stats.constitution,
    "  Dexterity: " + stats.dexterity,
  )
  private[this] def parseEquipment(eq: Equipment): Seq[String] =
    Vector("Equipment:") ++ eq.allSlots.map(e => s"  <${e._1}>: <${e._2}>")
  private def combatUnitLines(u: CombatUnit): List[String] = List(
    u.metadata.name,
    s"${u.hitPoints.currentHp}/${u.hitPoints.maxHp}",
  ) ++ u.safeCast[PlayerUnit].toIterator.flatMap(pu => parseStats(pu.stats) ++ parseEquipment(pu.equipment))
}
