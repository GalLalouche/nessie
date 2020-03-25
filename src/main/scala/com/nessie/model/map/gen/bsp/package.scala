package com.nessie.model.map.gen

import java.awt.Color

import com.nessie.model.map.{GridSize, MapPoint}

/*
 More or less copied from https://eskerda.com/bsp-dungeon-generation/.
 */
package object bsp {
  private[bsp] val ImageScale = 20
  private[bsp] def translate(mp: MapPoint) = MapPoint(mp.x * ImageScale, mp.y * ImageScale)
  private[bsp] def translate(gs: GridSize) = GridSize(gs.width * ImageScale, gs.height * ImageScale)
  private[bsp] val RoomColor = Color.GRAY
  private[bsp] val GridColor = Color.BLACK
}
