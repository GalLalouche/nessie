package com.nessie.model.map.fov

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed trait FogStatus extends EnumEntry
object FogStatus extends Enum[FogStatus] {
  override val values: immutable.IndexedSeq[FogStatus] = findValues

  case object Visible extends FogStatus
  case object Hidden extends FogStatus
  // Previously visited but no longer in the line of sight of any friendly unit. Enemy units under a fogged
  // cell are no longer visible but everything else (walls, terrain, items) is.
  case object Fogged extends FogStatus
}
