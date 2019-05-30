package com.nessie.view.zirconview.map

import com.nessie.model.map.MapPoint

private[zirconview] case class DistanceFromCenter(x: Int, y: Int)

private [zirconview] object DistanceFromCenter {
  def apply(center: MapPoint)(mp: MapPoint): DistanceFromCenter =
    DistanceFromCenter(mp.x - center.x, mp.y - center.y)
}
