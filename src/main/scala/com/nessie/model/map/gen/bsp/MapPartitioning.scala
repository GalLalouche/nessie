package com.nessie.model.map.gen.bsp

import java.awt.image.BufferedImage

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.RngableOption
import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.model.map.{BattleMap, Direction, GridSize, MapPoint}
import com.nessie.model.map.gen.DemoImageViewer._
import com.nessie.model.map.Direction.{Down, Right}
import com.nessie.model.map.gen.bsp.MapPartitioning.{Leaf, Tree}

import scalaz.OptionT
import monocle.syntax.apply._

import common.rich.RichT._

private class MapPartitioning private(val tree: Tree, gs: GridSize) {
  def toImage: BufferedImage = new BufferedImage(
    gs.width * ImageScale + 1, gs.height * ImageScale + 1, BufferedImage.TYPE_INT_ARGB
  ) <| tree.updateImage

  def toMap: BattleMap = ???
  def split: RngableOption[MapPartitioning] = tree.split.map(new MapPartitioning(_, gs))

  def getPartitions: List[Leaf] = tree.getPartitions.toList
}

private object MapPartitioning {
  private val MinDimensions = 3
  def apply(gs: GridSize): MapPartitioning = new MapPartitioning(Leaf(MapPoint(0, 0), gs), gs)
  sealed trait Tree {
    def center: MapPoint = topLeftCorner.go(Right, gs.width / 2).go(Down, gs.height / 2)
    def topLeftCorner: MapPoint
    def gs: GridSize

    def getPartitions: Iterator[Leaf]
    private[MapPartitioning] def updateImage($: BufferedImage): Unit

    private[MapPartitioning] def split: RngableOption[Tree]
  }

  def hasValidDimensions(gs: GridSize) =
    gs.height > MinDimensions && gs.width > MinDimensions &&
        2.2 >= math.max(gs.height, gs.width) / math.min(gs.height, gs.width)
  case class Leaf(
      override val topLeftCorner: MapPoint, override val gs: GridSize) extends Tree {
    assert(hasValidDimensions(gs))
    override private[MapPartitioning] def split = Rngable.tryNTimes(5) {
      val tree: Rngable[Option[Tree]] = for {
        isVertical <- mkRandom[Boolean]
        at <- Rngable.intRange(0, if (isVertical) gs.width else gs.height)
      } yield {
        val lens = if (isVertical) GridSize.width else GridSize.height
        val leftGs = gs &|-> lens set at
        val rightGs = gs &|-> lens modify (_ - at)
        Split(
          Leaf(topLeftCorner, leftGs),
          Leaf(topLeftCorner.go(if (isVertical) Direction.Right else Direction.Down, at), rightGs),
        ).onlyIf(hasValidDimensions(leftGs) && hasValidDimensions(rightGs))
      }
      OptionT(tree)
    }
    override private[MapPartitioning] def updateImage($: BufferedImage): Unit = {
      val mp = translate(topLeftCorner)
      val gs = translate(this.gs)
      def color(xRange: Iterable[Int], yRange: Iterable[Int]): Unit = for {
        x <- xRange.iterator
        y <- yRange.iterator
      } $.setRGB(x, y, GridColor.getRGB)
      color(mp.x to mp.x + gs.width, Vector(mp.y, mp.y + gs.height))
      color(Vector(mp.x, mp.x + gs.width), mp.y.to(mp.y + gs.height))
    }
    override def getPartitions = Iterator(this)
  }
  case class Split(t1: Tree, t2: Tree, private val startOnRight: Boolean = false) extends Tree {
    private val c1 = t1.center
    private val c2 = t2.center
    assert(c1.x == c2.x || c1.y == c1.y)

    override private[MapPartitioning] def split = {
      lazy val splitRight: RngableOption[Tree] = t2.split.map(Split(t1, _, startOnRight = true))
      if (startOnRight) splitRight else t1.split.map(Split(_, t2): Tree) orElse splitRight
    }
    override private[MapPartitioning] def updateImage($: BufferedImage): Unit = {
      t1.updateImage($)
      t2.updateImage($)
    }
    override def getPartitions = t1.getPartitions ++ t2.getPartitions
    override def topLeftCorner = {
      assert(t1.topLeftCorner.x <= t2.topLeftCorner.x)
      assert(t1.topLeftCorner.y <= t2.topLeftCorner.y)
      t1.topLeftCorner
    }
    override def gs =
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
