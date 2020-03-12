package com.nessie.model.map.gen.bsp

import java.awt.image.BufferedImage
import java.awt.Color

import com.nessie.model.map.{Direction, MapPoint}

private class JoinedRooms(r: Rooms) {
  def toImage: BufferedImage = {
    val $ = r.toImage
    val connections: Set[MapPoint] = r.mp.getConnections
    for {
      mp <- connections
      to = mp.go(Direction.Right).go(Direction.Down)
      mpt = translate(mp)
      tot = translate(to)
      x <- mpt.x until tot.x
      y <- mpt.y until tot.y
    } $.setRGB(x, y, Color.GRAY.getRGB)
    $
  }
}
private object JoinedRooms {
  def apply(r: Rooms) = new JoinedRooms(r)
}


