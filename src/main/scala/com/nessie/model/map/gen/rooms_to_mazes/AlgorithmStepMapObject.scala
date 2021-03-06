package com.nessie.model.map.gen.rooms_to_mazes

import com.nessie.model.map.{BattleMap, BattleMapObject}
import common.rich.collections.RichTraversableOnce._

private sealed trait AlgorithmStepMapObject extends BattleMapObject
// All the indices below are used for debugging and previewing the algorithm steps.
private case class RoomMapObject(index: Int) extends AlgorithmStepMapObject
private object RoomMapObject {
  /** Assumes non-overlapping rooms! */
  def getRooms(map: BattleMap): Map[Int, Room] = map.objects.collect {
    case (mp, RoomMapObject(i)) => i -> mp
  }.groupBy(_._1).mapValues(mps => {
    val (topLeft, bottomRight) = mps.map(_._2).toVector.range
    Room(x = topLeft.x, y = topLeft.y, w = bottomRight.x - topLeft.x + 1, h = bottomRight.y - topLeft.y + 1)
  })
}
private case class TunnelMapObject(index: Int) extends AlgorithmStepMapObject
private case class ReachableMapObject(original: AlgorithmStepMapObject, index: Int)
    extends AlgorithmStepMapObject
