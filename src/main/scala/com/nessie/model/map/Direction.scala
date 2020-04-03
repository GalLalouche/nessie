package com.nessie.model.map

import common.rich.primitives.RichBoolean._
import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed trait Direction extends EnumEntry {
  def isVertical: Boolean = this == Direction.Up || this == Direction.Down
  def isHorizontal: Boolean = isVertical.isFalse
  def opposite: Direction
}

object Direction extends Enum[Direction] {
  override val values: immutable.IndexedSeq[Direction] = findValues
  case object Up extends Direction {
    override def opposite = Down
  }
  case object Down extends Direction {
    override def opposite: Direction = Up
  }
  case object Left extends Direction {
    override def opposite: Direction = Right
  }
  case object Right extends Direction {
    override def opposite: Direction = Left
  }

  def from(mp1: MapPoint, mp2: MapPoint): Option[Direction] =
    if (mp1.manhattanDistanceTo(mp2) != 1) None else Some {
      mp1.y - mp2.y match {
        case -1 => Direction.Down
        case 0 => mp1.x - mp2.x match {
          case -1 => Direction.Right
          case 1 => Direction.Left
        }
        case 1 => Direction.Up
      }
    }
}
