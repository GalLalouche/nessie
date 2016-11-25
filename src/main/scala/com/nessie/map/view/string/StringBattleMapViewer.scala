package com.nessie.map.view.string

import com.nessie.gm.View
import com.nessie.map.{BattleMapObject, CombatUnitObject, EmptyMapObject}
import com.nessie.map.model.{BattleMap, MapPoint}
import com.nessie.units.{Skeleton, Warrior}

private class StringBattleMapViewer(m: BattleMap) extends View {
  override def toString = {
    val rows = 0 until m.height map (y => 0 until m.width map (x => m(MapPoint(x, y))))
    rows.map(_.map(print).mkString(",")).mkString("\n")
  }

  private def print(o: BattleMapObject): Char = o match {
    case EmptyMapObject => '_'
    case CombatUnitObject(u) => u match {
      case _: Warrior => 'W'
      case _: Skeleton => 'S'
    }
  }
  override def display(): Unit = {
    println(toString)
  }
}
