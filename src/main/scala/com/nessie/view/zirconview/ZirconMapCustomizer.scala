package com.nessie.view.zirconview

import com.nessie.model.map.BattleMapObject
import org.hexworks.zircon.api.builder.data.TileBuilder

trait ZirconMapCustomizer {
  def getTile: PartialFunction[BattleMapObject, TileBuilder]
}

object ZirconMapCustomizer {
  val Null: ZirconMapCustomizer = new ZirconMapCustomizer {
    override val toString = "NullCustomizer"
    override val getTile = PartialFunction.empty
  }
}
