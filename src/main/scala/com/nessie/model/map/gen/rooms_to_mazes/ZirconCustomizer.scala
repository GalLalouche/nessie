package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.view.zirconview.{ZirconMapCustomizer, ZirconViewCustomizer}
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.color.ANSITileColor

private object ZirconCustomizer extends ZirconViewCustomizer {
  override def mapCustomizer: ZirconMapCustomizer = new ZirconMapCustomizer {
    override val toString = "GenMapDemo customizer"
    override def getTile = {
      case RoomMapObject(i) =>
        Tiles.newBuilder().withCharacter(i.toString.head)
      case TunnelMapObject(i) =>
        Tiles.newBuilder().withCharacter(i.toString.head)
    }
  }
}
