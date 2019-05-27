package com.nessie.model.map

object BattleMapPrinter {
  private def print(o: BattleMapObject): Char = o match {
    case EmptyMapObject => '_'
    case FullWall => '*'
    case CombatUnitObject(u) => u.metadata.name.head
  }
  def apply(m: BattleMap): String = {
    val rows = 0.until(m.height).map(y => 0.until(m.width).map(x => m(MapPoint(x, y))))
    rows.map(_.map(print).mkString(",")).mkString("\n")
  }
}
