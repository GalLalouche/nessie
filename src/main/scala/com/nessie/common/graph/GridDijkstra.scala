package com.nessie.common.graph

import com.nessie.model.map.{Grid, MapPoint}
import com.nessie.model.map.fov.BresenhamsLine
import common.rich.primitives.RichBoolean._
import common.rich.RichT._
import common.rich.RichTuple._
import common.rich.collections.RichTraversableOnce._
import common.rich.CacheMap
import scalaz.std.vector.vectorInstance
import scalaz.syntax.functor._

import scala.collection.mutable

/**
 * A modified Dijkstra algorithms for grids with blocking cells, e.g., walls, assuming Euclidean distance
 * between cells if a direct line between two cells exists.
 * Also, unlike regular Dijkstra, we aren't interested in the actual path, but only in measuring the path's
 * length.
 *
 * For example, Consider the following map:
 * --*
 * ---
 *
 * One can get from (0, 0) to (1, 1) in a straight line—and therefore we could say that there exists an edge
 * with weight sqrt(2) from (0, 0) to (1, 1)—but not to (2, 1)—so is there no edge from (0, 0) to (2, 1)—but
 * there is one from (1, 1) to (2, 1), since they are neighbors in the grid. Therefore the quickest path to
 * (2, 1) is to go in a diagonal to (1, 1), and then go straight to (2, 1); a total distance of sqrt(2) + 1.
 */
object GridDijkstra {
  private class InitialDistances(map: Map[(MapPoint, MapPoint), Double]) {
    private def apply(p1: MapPoint, p2: MapPoint) = map.get(p1 -> p2).orElse(map.get(p2 -> p1))
    val neighbors = CacheMap[MapPoint, Iterable[(MapPoint, Double)]](
      mp => map.keysIterator.flatMap {
        case (`mp`, o) => Some(o)
        case (o, `mp`) => Some(o)
        case _ => None
      }.toVector.fproduct(apply(_, mp).get))
  }

  trait Blockable[A] {
    def blocks(a: A): Boolean
    final def unimpededStraightLine(grid: Grid[A])(source: MapPoint, target: MapPoint): Boolean =
      BresenhamsLine.thick(source, target).map(grid.apply).fornone(blocks)
  }
  object Blockable {
    def apply[A](f: A => Boolean): Blockable[A] = f(_)
  }

  private def apply[A: Blockable](
      grid: Grid[A], source: MapPoint, maxDistance: Option[Int], target: Option[MapPoint]
  ): Map[MapPoint, Double] = {
    val ev = implicitly[Blockable[A]]
    val unimpeded = CacheMap[(MapPoint, MapPoint), Boolean](Function.tupled(ev.unimpededStraightLine(grid)))
    val vertices = maxDistance match {
      case None => for {x <- 0 until grid.width; y <- 0 until grid.height} yield MapPoint(x = x, y = y)
      case Some(md) => for {
        x <- source.x - md to source.x + md
        y <- source.y - md to source.y + md
        mp = MapPoint(x, y)
        if grid.isInBounds(mp)
      } yield mp
    }

    // The initial distance between two unimpeded cells is the Euclidean distance between them.
    val initialDistances =
      vertices.unorderedPairs.filter(unimpeded).map(_.:->(_.reduce(_ euclideanDistanceTo _))).toMap |>
          (new InitialDistances(_))
    val priorityQueue = new mutable.PriorityQueue[(MapPoint, Double)]()(Ordering.by(-_._2))
    val $ = mutable.Map[MapPoint, Double]().withDefault(Double.PositiveInfinity.const)
    for (mp <- vertices) {
      val distance = if (unimpeded(source, mp)) source.euclideanDistanceTo(mp) else Double.PositiveInfinity
      $(mp) = distance
      priorityQueue += mp -> distance
    }

    while (priorityQueue.nonEmpty && target.contains(priorityQueue.head._1).isFalse) {
      val (next, nextDistance) = priorityQueue.dequeue()
      for ((neighbor, neighborDistance) <- initialDistances.neighbors(next)) {
        val alternative = nextDistance + neighborDistance
        if (alternative < $(neighbor)) {
          $(neighbor) = alternative
          priorityQueue += neighbor -> alternative
        }
      }
    }

    ($ - source).filterNot(e => e._2.isPosInfinity || maxDistance.exists(e._2.>)).toMap
  }

  def apply[A: Blockable](grid: Grid[A], source: MapPoint, target: MapPoint): Option[Double] =
    apply(grid, source, None, Some(target)).get(target)
  def apply[A: Blockable](grid: Grid[A], source: MapPoint, maxDistance: Int): Map[MapPoint, Double] =
    apply(grid, source, Some(maxDistance), None)
}
