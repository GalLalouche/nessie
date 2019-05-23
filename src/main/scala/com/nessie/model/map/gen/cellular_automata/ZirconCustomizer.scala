package com.nessie.model.map.gen.cellular_automata

import com.nessie.view.zirconview.{ZirconMapCustomizer, ZirconViewCustomizer}
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.color.ANSITileColor

object ZirconCustomizer extends ZirconViewCustomizer {
  override def mapCustomizer = new ZirconMapCustomizer {
    override def getTile = {
      case Empty(n) => Tiles.newBuilder().withCharacter(n.toString.last)
      case Wall(_) => Tiles.newBuilder().withCharacter('*')
      case CaveMapObject(id) => Tiles.newBuilder().withCharacter(id)
          .withForegroundColor(ANSITileColor.BRIGHT_MAGENTA)
      case Tunnel => Tiles.newBuilder().withCharacter('-')
          .withForegroundColor(ANSITileColor.BRIGHT_CYAN)
    }
  }
}
