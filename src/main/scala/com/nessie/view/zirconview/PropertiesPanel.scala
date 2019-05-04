package com.nessie.view.zirconview

import com.nessie.model.map.{BattleMap, CombatUnitObject, MapPoint}
import com.nessie.model.units.{CombatUnit, PlayerUnit}
import com.nessie.model.units.inventory.Equipment
import com.nessie.model.units.stats.Stats
import common.rich.RichT._
import org.hexworks.zircon.api.{Components, Positions}
import org.hexworks.zircon.api.builder.component.{PanelBuilder, TextBoxBuilder}
import org.hexworks.zircon.api.component.{Component, Panel}

private class PropertiesPanel private(panel: Panel) {
  panel.applyColorTheme(ZirconConstants.Theme)
  def update(map: BattleMap)(mp: Option[MapPoint]): Unit = {
    panel.clear()
    mp.foreach(mp => panel.addComponent({
      val textBox = Components.textBox()
          .withContentWidth(panel.getWidth - 2)
          .addHeader(mp.toString)
      map(mp).safeCast[CombatUnitObject].map(_.unit).foreach(PropertiesPanel.addUnitProperties(textBox))
      textBox
          .withPosition(Positions.zero().relativeToTopOf(panel))
          .build()
    }))
    panel.applyColorTheme(ZirconConstants.Theme)
  }
  def component: Component = panel
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
  def create(panelBuilder: PanelBuilder => Any): PropertiesPanel = new PropertiesPanel(Components
      .panel()
      .withTitle("Properties pane")
      .wrapWithBox(true)
      .<|(panelBuilder)
      .build()
  )
}
