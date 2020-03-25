package com.nessie.model.map.gen.bsp

import java.awt.image.BufferedImage

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.{RngableIterable, RngableOption}
import com.nessie.model.map.{BattleMap, Direction, GridSize, MapPoint}
import com.nessie.model.map.gen.bsp.MapPartitioning.Leaf
import com.nessie.model.map.gen.bsp.Rooms.Room

import scalaz.syntax.monad.ToMonadOps
import scalaz.OptionT

import common.rich.RichT._

private class Rooms private(
    val mp: MapPartitioning,
    rooms: List[Room],
    remainingPartitions: List[Leaf]) {
  def toImage: BufferedImage = mp.toImage.<|(i => rooms.foreach(_.updateImage(i)))
  def toBattleMap: BattleMap = ???
  def partitions: Iterable[Leaf] = rooms.map(_.p)
  private def next: RngableOption[Rooms] = remainingPartitions match {
    case Nil => Rngable.none
    case head :: tl =>
      val maxHorizontalPad = head.gs.width / 3
      val maxVerticalPad = head.gs.height / 3
      (for {
        padLeft <- Rngable.intRange(0, maxHorizontalPad)
        padRight <- Rngable.intRange(0, maxHorizontalPad)
        padTop <- Rngable.intRange(0, maxVerticalPad)
        padBottom <- Rngable.intRange(0, maxVerticalPad)
      } yield {
        val roomTopLeft = head.topLeftCorner
            .go(Direction.Down, padTop)
            .go(Direction.Right, padLeft)
        val width = head.gs.width - padLeft - padRight
        val height = head.gs.height - padTop - padBottom
        val roomSize = GridSize(width = width, height = height)
        new Rooms(mp, Room(head, roomTopLeft, roomSize) :: rooms, tl)
      }).liftM[OptionT]
  }
}
private object Rooms {
  private case class Room(p: Leaf, roomTopLeftCorner: MapPoint, roomSize: GridSize) {
    def updateImage($: BufferedImage): Unit = {
      val tCorner = translate(roomTopLeftCorner)
      val tSize = translate(roomSize)
      for {
        x <- tCorner.x until tCorner.x + tSize.width
        y <- tCorner.y until tCorner.y + tSize.height
      } $.setRGB(x, y, RoomColor.getRGB)
    }
  }
  def apply(mp: MapPartitioning): RngableIterable[Rooms] =
    Rngable.iterateOptionally(new Rooms(mp, Nil, mp.getPartitions))(_.next)
}
