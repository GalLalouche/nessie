package com.nessie.view.swing

import java.awt.Color
import javax.swing.border._

import common.rich.RichT._
import com.nessie.map.model.{BattleMap, BattleMapController, MapPoint}
import com.nessie.map.objects.CombatUnitObject
import com.nessie.model.map.objects.{BattleMapObject, EmptyMapObject}
import com.nessie.units.Warrior
import com.nessie.view.map.{MapView, MapViewFactory}
import com.nessie.view.swing.RichGrid.ItemClicked

import scala.swing._

class SwingMapView(map: BattleMap, controller: BattleMapController) extends MapView {

  import SwingMapView._

  var selectedPoint: Option[MapPoint] = None
  private def createButton(p: MapPoint, o: BattleMapObject): Component = {
    val shortName: String = o match {
      case EmptyMapObject => ""
      case CombatUnitObject(u) => u.getClass.getSimpleName.substring(0, 1)
    }
    new Button(shortName) {
      preferredSize = new Dimension(50, 50)
    }
  }
  val grid = RichGrid(map.height, map.width, map.map(e => createButton(e._1, e._2)).toSeq)
  grid.reactions += {
    case ItemClicked(_, x, y) => clicked((x, y))
  }
  private def select(p: MapPoint) {
    if (false == map.isOccupiedAt(p))
      return
    label.text = map(p).asInstanceOf[CombatUnitObject].u.statusString
    label.repaint()
    selectedPoint = Some(p)
    val width = 5
    grid(p.x, p.y).border = new MatteBorder(width, width, width, width, Color.red)
  }
  private def unselect(p: MapPoint) = {
    selectedPoint = None
    grid(p.x, p.y).border = defaultBorder
  }

  val label = new Label("Texty")
  private def clicked(p: MapPoint) {
    for (selected <- selectedPoint) {
      unselect(selected)
      if (selected == p)
        return
      else if (!map.isOccupiedAt(p))
        controller move selected to p
      else if (map(p).safeCast[CombatUnitObject].map(_.u.isInstanceOf[Warrior]).getOrElse(false))
        map(p).asInstanceOf[CombatUnitObject].u.getAttacked(map(selected).asInstanceOf[CombatUnitObject].u.getBasicAttack)
  }
  select(p)
}
val frame = new MainFrame () {
contents = {
new BorderPanel {
add (grid, BorderPanel.Position.Center)
add (label, BorderPanel.Position.South)
}
}
}
override def start () {
frame.visible = true
}
override def stop () {
frame.close ()
}
}

object SwingMapView extends MapViewFactory {
  override def apply(m: BattleMap, c: BattleMapController) = new SwingMapView(m, c)
  private val defaultBorder = new Button("temp").border
}
