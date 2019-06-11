package com.nessie.model.map

import com.nessie.model.map.GridPrinter.CharPrintable

object BattleMapPrinter {
  implicit val battleMapObjectEv: CharPrintable[BattleMapObject] = CharPrintable.apply {
    case EmptyMapObject => '_'
    case FullWall => '*'
    case CombatUnitObject(u) => u.metadata.name.head
  }
  def apply(m: BattleMap): String = GridPrinter(m.grid)
}
