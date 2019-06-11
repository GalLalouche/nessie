package com.nessie.model.map

import com.nessie.model.map.GridParser.CharParsable
import common.rich.RichT._

trait BattleMapParser {
  def parse(s: String): BattleMap
}

object BattleMapParser {
  private implicit val BattleMapObjectEv: CharParsable[BattleMapObject] = CharParsable {
    case '*' => FullWall
    case '_' => EmptyMapObject
  }

  def fromFactory(gf: GridFactory): BattleMapParser = GridParser.fromFactory(gf).parse(_) |> BattleMap.apply
}
