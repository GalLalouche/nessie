package com.nessie.model.map.gen.bsp

import java.awt.image.BufferedImage
import java.awt.Color

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.{RngableIterable, RngableOption}
import com.nessie.common.rng.Rngable.ToRngableOps._
import com.nessie.model.map.{Direction, MapPoint}
import com.nessie.model.map.gen.bsp.MapPartitioning.{Split, Tree}

import common.rich.RichT._
import common.uf.ImmutableUnionFind

private case class JoinedRooms private(
    rooms: Rooms, maxWidth: Int, connections: Set[MapPoint], uf: ImmutableUnionFind[MapPoint]) {
  private def findUnconnectedSplit: Split = {
    def go(t: Tree): Option[Split] = t match {
      case s@Split(t1, t2, _) =>
        if (uf.sameSet(t1.topLeftCorner, t2.topLeftCorner))
          None
        else
          Some(go(t1).orElse(go(t2)).getOrElse(s))
      case _ => None
    }
    go(rooms.mp.tree).get
  }

  private def connect(nextSplit: Split, width: Int, evenTieBreaker: Boolean): Iterator[MapPoint] = {
    def pointAround(x: Int, y: Int, vertical: Boolean): Iterator[MapPoint] = {
      val (left, right) = (math.max(0, (width - 2) / 2) -> width / 2).mapIf(evenTieBreaker).to(_.swap)
      if (vertical)
        (y - left).to(y + right).iterator.map(MapPoint(x, _))
      else
        (x - left).to(x + right).iterator.map(MapPoint(_, y))
    }
    val c1 = nextSplit.t1.center
    val c2 = nextSplit.t2.center
    if (c1.x == c2.x)
      c1.y.to(c2.y).iterator.flatMap(y => pointAround(x = c1.x, y = y, vertical = false))
    else {
      assert(c1.y == c2.y, c1 -> c2)
      c1.x.to(c2.x).iterator.flatMap(x => pointAround(x = x, y = c1.y, vertical = true))
    }
  }

  private def next: RngableOption[JoinedRooms] = Rngable.unless(uf.hasSingleSet) {
    val nextSplit = findUnconnectedSplit
    val nextUf = uf.union(nextSplit.t1.topLeftCorner, nextSplit.t2.topLeftCorner)
    for {
      width <- Rngable.intRange(1, maxWidth + 1)
      evenTieBreaker <- if (width % 2 != 0) Rngable.pure(false) else mkRandom[Boolean]
    } yield JoinedRooms(rooms, maxWidth, connections ++ connect(nextSplit, width, evenTieBreaker), nextUf)
  }

  require(maxWidth >= 1)

  private def toImage(connectionColor: Color): BufferedImage = {
    val $ = rooms.toImage
    for {
      mp <- connections
      to = mp.go(Direction.Right).go(Direction.Down)
      mpt = translate(mp)
      tot = translate(to)
      x <- mpt.x until tot.x
      y <- mpt.y until tot.y
    } $.setRGB(x, y, connectionColor.getRGB)
    $
  }
  def toImage: BufferedImage = toImage(Color.DARK_GRAY)
  def toPlainImage: BufferedImage = {
    val $ = toImage(RoomColor)
    for {
      x <- 0 until $.getWidth
      y <- 0 until $.getHeight
      if $.getRGB(x, y) == GridColor.getRGB
    } $.setRGB(x, y, Color.WHITE.getRGB)
    $
  }
}
private object JoinedRooms {
  private type RoomConnection = Set[MapPoint]
  def apply(r: Rooms, maxWidth: Int = 1): RngableIterable[JoinedRooms] = {
    val baseUf = ImmutableUnionFind(r.partitions.map(_.topLeftCorner))
    Rngable.iterateOptionally(new JoinedRooms(r, maxWidth, Set.empty, baseUf))(_.next)
  }
}


