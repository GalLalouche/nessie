package com.nessie.model.map.fov

import com.nessie.model.map.{BattleMap, Grid, GridLike, MapPoint}
import com.nessie.model.map.fov.FogStatus.{Fogged, Hidden, Visible}
import monocle.Lens
import monocle.macros.Lenses

@Lenses
case class FogOfWar private(grid: Grid[FogStatus]) extends GridLike[FogOfWar, FogStatus] {
  override protected def gridLens: Lens[FogOfWar, Grid[FogStatus]] = FogOfWar.grid

  lazy val currentVisible: Set[MapPoint] = objects.filter(_._2 == Visible).map(_._1).toSet
  /**
   * All points will be set to visible, all previously visible points which are no longer visible will be set
   * to Fogged.
   */
  def updateVisible(mps: Set[MapPoint]): FogOfWar = mapPoints((p, o) => if (mps(p)) Visible else o match {
    case FogStatus.Visible | FogStatus.Fogged => Fogged
    case FogStatus.Hidden => Hidden
  })
}

object FogOfWar {
  import scalaz.syntax.functor._

  def allVisible(map: BattleMap): FogOfWar = FogOfWar(map.grid >| Visible)
  def allHidden(map: BattleMap): FogOfWar = FogOfWar(map.grid >| Hidden)
}
