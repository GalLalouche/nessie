package com.nessie.view.zirconview.screen

import com.nessie.gm.GameState
import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.{CombatUnit, PlayerUnit}
import com.nessie.model.units.inventory.Equipment
import com.nessie.model.units.stats.Stats
import com.nessie.view.zirconview.{ComponentWrapper, MapPointHighlighter, PanelPlacer, TextBoxPanel}
import common.rich.RichT._
import org.hexworks.zircon.api.builder.component.TextBoxBuilder
import org.hexworks.zircon.api.component.Component

private class PropertiesPanel private(panel: TextBoxPanel) extends ComponentWrapper {
  def update(map: BattleMap)(mp: Option[MapPoint]): Unit = synchronized {
    clear()
    mp.foreach {mp =>
      panel.update {textBox =>
        textBox.addHeader(mp.toString)
        map(mp).safeCast[CombatUnitObject].map(_.unit).foreach(PropertiesPanel.addUnitProperties(textBox))
      }
    }
  }
  def clear(): Unit = synchronized {
    panel.clear()
  }
  val highlighter: MapPointHighlighter = new MapPointHighlighter {
    override def apply(gs: GameState, mp: MapPoint): Unit = update(gs.map)(Some(mp))
    override def clear(): Unit = PropertiesPanel.this.clear()
  }
  override def component: Component = panel.component
}

private object PropertiesPanel {
  private def addUnitProperties(tb: TextBoxBuilder)(u: CombatUnit): Unit = {
    def parseStats(stats: Stats): Unit = {
      tb.addHeader("Stats")
      tb.addListItem("Strength: " + stats.strength)
      tb.addListItem("Constitution: " + stats.constitution)
      tb.addListItem("Dexterity: " + stats.dexterity)
    }
    def parseEquipment(eq: Equipment): Unit = {
      tb.addHeader("Equipment")
      eq.allSlots.foreach(e => tb.addListItem(s"<${e._1}>: <${e._2}>"))
    }
    tb.addHeader(u.metadata.name)
    tb.addHeader(s"${u.hitPoints.currentHp}/${u.hitPoints.maxHp}")
    u.safeCast[PlayerUnit].foreach(pu => {parseStats(pu.stats); parseEquipment(pu.equipment)})
  }

  def create(panelPlacer: PanelPlacer) = new PropertiesPanel(TextBoxPanel("Properties", panelPlacer))
}
