package com.nessie.model.map.gen.bsp

import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.common.rng.StdGen
import com.nessie.model.map.{gen, GridSize}

private object BspDemo extends gen.DemoImageViewer {
  override def mapFunction = {
    val iterator = StdGen(0).iterator
    val generator = new Generator(GridSize(50, 50))
    lazy val base = generator.partitions.mkRandom(iterator.next())
    lazy val rooms = Rooms(base.last).mkRandom(iterator.next())
    lazy val joinedRooms = JoinedRooms(rooms.last, maxWidth = 5).mkRandom(iterator.next())

    {
      case 0 => base.map(_.toImage)
      case 1 => rooms.map(_.toImage)
      case 2 => joinedRooms.map(_.toImage)
      case 3 => Stream(joinedRooms.last.toPlainImage)
    }
  }
}
