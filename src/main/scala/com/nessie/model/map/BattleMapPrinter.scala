package com.nessie.model.map

import com.nessie.model.map.GridPrinter.CharPrintable

object BattleMapPrinter {
  def apply(
      m: BattleMap,
      additionalCharacters: PartialFunction[BattleMapObject, Char] = PartialFunction.empty,
  ): String = {
    implicit val battleMapObjectEv: CharPrintable[BattleMapObject] = CharPrintable.apply {o =>
      additionalCharacters.lift(o).getOrElse {
        o match {
          case EmptyMapObject => '_'
          case FullWall => '*'
          case CombatUnitObject(u)
          => u.metadata.name.head
        }
      }
    }
    GridPrinter(m.grid)
  }
}
