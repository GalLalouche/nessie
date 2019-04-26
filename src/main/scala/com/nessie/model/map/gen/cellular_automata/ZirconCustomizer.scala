package com.nessie.model.map.gen.cellular_automata

import com.nessie.view.zirconview.{ZirconMapCustomizer, ZirconViewCustomizer}
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.graphics.Symbols

object ZirconCustomizer extends ZirconViewCustomizer {
  override def mapCustomizer = new ZirconMapCustomizer {
    override def getTile = {
      case Empty(_) => Tiles.newBuilder().withCharacter('-')
      case Wall(_) => Tiles.newBuilder().withCharacter('#')
    }
  }
}
