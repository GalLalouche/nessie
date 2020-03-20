package com.nessie.model.map.gen.bsp

import java.awt.image.BufferedImage
import java.awt.Color

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.RngableOption
import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.model.map.{BattleMap, GridSize, MapPoint}
import com.nessie.model.map.Direction.{Down, Right}
import com.nessie.model.map.gen.bsp.MapPartitioning.Tree

import scalaz.OptionT

import common.rich.RichT._

private class MapPartitioning private(tree: Tree, gs: GridSize) {
  def getConnections: Set[MapPoint] = tree.getConnections

  def toImage: BufferedImage = new BufferedImage(
    gs.width * ImageScale + 1, gs.height * ImageScale + 1, BufferedImage.TYPE_INT_ARGB
  ) <| tree.updateImage

  def toMap: BattleMap = ???
  def split: RngableOption[MapPartitioning] = tree.split.map(new MapPartitioning(_, gs))

  def getPartitions: List[Partition] = tree.toPartitions.toList
}

private object MapPartitioning {
  private val MinDimensions = 3
  def apply(gs: GridSize): MapPartitioning = new MapPartitioning(Empty(MapPoint(0, 0), gs), gs)
  private sealed trait Tree {
    def center = topLeftCorner.go(Right, gs.width / 2).go(Down, gs.height / 2)
    def topLeftCorner: MapPoint
    def gs: GridSize

    def getConnections: Set[MapPoint]

    def toPartitions: Iterator[Partition]
    def updateImage($: BufferedImage): Unit

    def split: RngableOption[Tree]
  }

  def hasValidDimensions(gs: GridSize) =
    gs.height > MinDimensions && gs.width > MinDimensions &&
        2.2 >= math.max(gs.height, gs.width) / math.min(gs.height, gs.width)
  private case class Empty(
      override val topLeftCorner: MapPoint, override val gs: GridSize) extends Tree {
    assert(hasValidDimensions(gs))
    override def split = Rngable.tryNTimes(5) {
      val tree: Rngable[Option[Tree]] = for {
        isVertical <- mkRandom[Boolean]
        at <- Rngable.intRange(0, if (isVertical) gs.width else gs.height)
      } yield
        if (isVertical) {
          val leftGs = gs.copy(width = at)
          val rightGs = GridSize.width.modify(_ - at)(gs)
          if (hasValidDimensions(leftGs) && hasValidDimensions(rightGs))
            Some(Split(
              Empty(topLeftCorner, leftGs),
              Empty(topLeftCorner.go(Right, at), rightGs),
            ))
          else
            None
        } else {
          val leftGs = gs.copy(height = at)
          val rightGs = GridSize.height.modify(_ - at)(gs)
          if (hasValidDimensions(leftGs) && hasValidDimensions(rightGs))
            Some(Split(
              Empty(topLeftCorner, leftGs),
              Empty(topLeftCorner.go(Down, at), rightGs),
            ))
          else
            None
        }
      OptionT(tree)
    }
    override def updateImage($: BufferedImage): Unit = {
      val mp = translate(topLeftCorner)
      val gs = translate(this.gs)
      for {
        x <- mp.x to (mp.x + gs.width)
        y <- Vector(mp.y, mp.y + gs.height)
      } $.setRGB(x, y, Color.BLACK.getRGB)
      for {
        y <- mp.y to (mp.y + gs.height)
        x <- Vector(mp.x, mp.x + gs.width)
      } $.setRGB(x, y, Color.BLACK.getRGB)
    }
    override def toPartitions = Iterator(Partition(topLeftCorner, gs))
    override def getConnections = Set()
  }
  private case class Split(t1: Tree, t2: Tree, startOnRight: Boolean = false) extends Tree {
    val c1 = t1.center
    val c2 = t2.center
    assert(c1.x == c2.x || c1.y == c1.y)

    private def splitRight: RngableOption[Tree] = t2.split.map(Split(t1, _, startOnRight = true))
    override def split =
      if (startOnRight) splitRight else t1.split orElse splitRight
    override def updateImage($: BufferedImage): Unit = {
      t1.updateImage($)
      t2.updateImage($)
    }
    override def toPartitions = t1.toPartitions ++ t2.toPartitions

    def connect(c1: MapPoint, c2: MapPoint): Iterable[MapPoint] = {
      if (c1.x == c2.x)
        c1.y.to(c2.y).map(MapPoint(c1.x, _))
      else {
        assert(c1.y == c2.y, c1 -> c2)
        c1.x.to(c2.x).map(MapPoint(_, c1.y))
      }
    }

    override def getConnections = t1.getConnections ++ t2.getConnections ++
        connect(t1.center, t2.center)
    override def topLeftCorner = {
      assert(t1.topLeftCorner.x <= t2.topLeftCorner.x)
      assert(t1.topLeftCorner.y <= t2.topLeftCorner.y)
      t1.topLeftCorner
    }
    override def gs = {
      if (t1.topLeftCorner.x == t2.topLeftCorner.x) {
        assert(t1.gs.width == t2.gs.width)
        GridSize(width = t1.gs.width, height = t1.gs.height + t2.gs.height)
      } else {
        assert(t1.topLeftCorner.y == t2.topLeftCorner.y)
        assert(t1.gs.height == t2.gs.height)
        GridSize(width = t1.gs.width + t2.gs.width, height = t1.gs.height)
      }
    }
  }
}
