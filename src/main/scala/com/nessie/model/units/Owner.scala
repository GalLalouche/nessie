package com.nessie.model.units

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed trait Owner extends EnumEntry

object Owner extends Enum[Owner] {
  val values: immutable.IndexedSeq[Owner] = findValues
  case object AI extends Owner
  case object Player extends Owner
}
