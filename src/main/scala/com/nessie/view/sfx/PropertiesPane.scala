package com.nessie.view.sfx

import javafx.scene.control.Label

import com.nessie.gm.GameState
import com.nessie.model.map.{BattleMapObject, CombatUnitObject, EmptyMapObject, MapPoint}
import common.rich.RichT._

import scala.collection.JavaConversions._
import scalafx.scene.layout.VBox

private class PropertiesPane(gs: GameState) extends NodeWrapper {
  private def getProperties(mapObject: BattleMapObject): Seq[javafx.scene.Node] = mapObject match {
    case EmptyMapObject => List()
    case CombatUnitObject(u) => List(u.simpleName, s"${u.currentHp}/${u.maxHp}").map(new Label(_))
  }

  def display(p: MapPoint): Unit = {
    node.children.setAll(getProperties(gs.map(p)))
  }

  val node = new VBox()
}
