package com.nessie.gm

import com.nessie.common.MonocleUtils
import com.nessie.model.map.{BattleMap, CombatUnitObject}
import com.nessie.model.map.fov.{FogOfWar, TeamFov}
import com.nessie.model.units.{CombatUnit, Owner}

import common.rich.func.MoreIterableInstances._
import common.rich.func.ToMoreMonadPlusOps._
import monocle.Lens
import monocle.macros.Lenses

import common.rich.RichT._

@Lenses
case class MapAndFogs(
    map: BattleMap,
    fogsForOwner: Map[Owner, FogOfWar],
) {
  def units: Iterable[CombatUnit] = map.objects.map(_._2).select[CombatUnitObject].map(_.unit)
}

object MapAndFogs {
  import scalaz.syntax.functor._
  import common.rich.func.MoreIteratorInstances._

  def fogForOwner(o: Owner): Lens[MapAndFogs, FogOfWar] =
    MonocleUtils.unsafeCovariance(MapAndFogs.fogsForOwner ^|-> MonocleUtils.unsafeMapLens(o))

  private def fromFogs(map: BattleMap, f: Owner => FogOfWar): MapAndFogs = {
    val fogs = map.owners.iterator.fproduct(f).toMap
    MapAndFogs(map, fogs)
  }
  def teamFov(map: BattleMap): MapAndFogs = {
    fromFogs(map, o => FogOfWar.allHidden(map).updateVisible(TeamFov.visibleForOwner(o, map)))
  }
  def allVisible(map: BattleMap): MapAndFogs = {
    fromFogs(map, FogOfWar.allVisible(map).const)
  }
}
