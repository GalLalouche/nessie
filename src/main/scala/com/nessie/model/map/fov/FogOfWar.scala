package com.nessie.model.map.fov

import com.nessie.model.map.{BattleMap, Grid, GridLike, MapPoint, VectorGrid}
import com.nessie.model.map.fov.FogStatus.{Fogged, Hidden, Visible}
import monocle.Lens
import monocle.macros.Lenses

@Lenses
case class FogOfWar private(map: BattleMap, grid: Grid[FogStatus]) extends GridLike[FogOfWar, FogStatus] {
  override protected def gridLens: Lens[FogOfWar, Grid[FogStatus]] = FogOfWar.grid

  lazy val currentVisible: Set[MapPoint] = objects.filter(_._2 == Visible).map(_._1).toSet
  /**
   * All points will be set to visible, all previously visible points which are no longer visible will be set
   * to Fogged.
   */
  def updateVisible(mps: Set[MapPoint]): FogOfWar =
    foldPoints((fow, p) => fow.place(p, if (mps(p)) Visible else grid(p) match {
      case FogStatus.Visible | FogStatus.Fogged => Fogged
      case FogStatus.Hidden => Hidden
    }))
}
object FogOfWar {
  // TODO use the same grid as BattleMap
  def allVisible(map: BattleMap): FogOfWar = FogOfWar(map, VectorGrid(map.size, Visible))
  def allHidden(map: BattleMap): FogOfWar = FogOfWar(map, VectorGrid(map.size, Hidden))
}
