package com.nessie.model.map.fov

import com.nessie.model.map.{BattleMap, MapPoint}
import common.rich.collections.RichTraversableOnce._

class FovCalculator(map: BattleMap) {
  def isVisibleFrom(src: MapPoint, dst: MapPoint): Boolean =
  // Adjacent tiles are always visible
    src.manhattanDistanceTo(dst) <= 1 ||
        // init is needed since the ending tile is always visible, i.e., one can always see the blocking wall.
        // tail is needed for debugging to see what's visible from a tile wall.
        BresenhamsLine(src, dst).toVector.tail.init.fornone(map(_).obstructsVision)

  def getVisiblePointsFrom(mp: MapPoint, visionRange: Int): Iterable[MapPoint] = for {
    x <- mp.x.-(visionRange) to mp.x.+(visionRange)
    y <- mp.y.-(visionRange) to mp.y.+(visionRange)
    p = MapPoint(x, y)
    if map.isInBounds(p)
    if mp.euclideanDistanceTo(p) <= visionRange
    if isVisibleFrom(mp, p)
  } yield p
}

object FovCalculator {
  def apply(map: BattleMap): FovCalculator = new FovCalculator(map)
}