package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.view.sfx.{ScalaFxMapCustomizer, ScalaFxViewCustomizer}

object ScalaFxCustomizer extends ScalaFxViewCustomizer {
  override def mapCustomizer = new ScalaFxMapCustomizer {
    override def text = {
      case TunnelMapObject(i) => i.toString
      case RoomMapObject(i) => "R" + i
      case ReachableMapObject(o, i) => o match {
        case RoomMapObject(i2) => s"R$i2,$i"
        case TunnelMapObject(i2) => s"T$i2,$i"
      }
    }
    override def cellColor = {
      case TunnelMapObject(_) => "yellow"
      case ReachableMapObject(o, _) => o match {
        case _: TunnelMapObject => "orange"
        case _: RoomMapObject => "cyan"
      }
    }
  }
}