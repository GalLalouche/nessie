package com.nessie.model.map.gen.bsp

import java.awt.image.BufferedImage
import java.awt.Color

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.{RngableIterable, RngableOption}
import com.nessie.model.map.{BattleMap, Direction, GridSize, MapPoint}
import com.nessie.model.map.gen.bsp.Rooms.Room

import scalaz.syntax.monad.ToMonadOps
import scalaz.OptionT

import common.rich.RichT._

private class Rooms private(
    val mp: MapPartitioning,
    rooms: List[Room],
    remainingPartitions: List[Partition]) {
  def toImage: BufferedImage = mp.toImage.<|(i => rooms.foreach(_.updateImage(i)))
  def toBattleMap: BattleMap = ???
  def next: RngableOption[Rooms] = remainingPartitions match {
    case Nil => Rngable.none
    case head :: tl => (for {
      padLeft <- Rngable.intRange(0, head.size.width / 3)
      padRight <- Rngable.intRange(0, head.size.width / 3)
      padTop <- Rngable.intRange(0, head.size.height / 3)
      padBottom <- Rngable.intRange(0, head.size.width / 3)
      roomTopLeft = head.topLeftCorner.go(Direction.Down, padTop)
          .go(Direction.Right, padLeft)
    } yield {
      val width = head.size.width - padLeft - padRight
      val height = head.size.height - padTop - padBottom
      val roomSize = GridSize(width, height)
      val room = Room(head, roomTopLeft, roomSize)
      new Rooms(mp, room :: rooms, tl)
    }).liftM[OptionT]
  }
}
private object Rooms {
  private case class Room(
      p: Partition,
      roomTopLeftCorner: MapPoint, roomSize: GridSize,
  ) {
    def updateImage($: BufferedImage): Unit = {
      val tCorner = translate(roomTopLeftCorner)
      val tSize = translate(roomSize)
      for {
        x <- tCorner.x until tCorner.x + tSize.width
        y <- tCorner.y until tCorner.y + tSize.height
      } $.setRGB(x, y, Color.GRAY.getRGB)
    }
  }
  def apply(mp: MapPartitioning): RngableIterable[Rooms] =
    Rngable.iterateOptionally(new Rooms(mp, Nil, mp.getPartitions))(_.next)
}
