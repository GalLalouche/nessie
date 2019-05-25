package com.nessie.model.map

import com.nessie.common.graph.RichUndirected._
import common.rich.RichT._
import common.rich.RichTuple._
import common.rich.func.{MoreIterableInstances, MoreSetInstances}
import common.rich.primitives.RichBoolean._
import monocle.macros.Lenses
import monocle.Lens
import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge

import scalaz.syntax.ToFunctorOps

/** An map of a given level without between-objects, so walls and its ilks taken up a full tile. */
@Lenses
case class BattleMap private(grid: Grid[BattleMapObject])
    extends GridLike[BattleMap, BattleMapObject]
        with ToFunctorOps with MoreSetInstances with MoreIterableInstances {
  override protected def gridLens: Lens[BattleMap, Grid[BattleMapObject]] = BattleMap.grid

  // Using eq for singletons is potentially faster.
  def isEmptyAt(p: MapPoint): Boolean = EmptyMapObject eq apply(p)
  def isOccupiedAt(p: MapPoint): Boolean = isEmptyAt(p).isFalse
  def remove(p: MapPoint): BattleMap = place(p, EmptyMapObject)

  private def reachableNeighbors(mp: MapPoint): Iterable[MapPoint] =
    if (this (mp).canMoveThrough) neighbors(mp).filter((apply _).andThen(_.canMoveThrough)) else Nil

  lazy val toObjectGraph: Graph[(MapPoint, BattleMapObject), UnDiEdge] = {
    val nodes: Iterable[(MapPoint, BattleMapObject)] = objects
    val edges: Iterable[UnDiEdge[(MapPoint, BattleMapObject)]] = points
        .flatMap(o => reachableNeighbors(o).map(o -> _))
        .map(_.map(_ :-> apply).reduce(UnDiEdge.apply))
    Graph.from(nodes, edges)
  }

  lazy val toPointGraph: Graph[MapPoint, UnDiEdge] = toObjectGraph.mapNodes(_._1)
  lazy val passablePointGraph: Graph[MapPoint, UnDiEdge] =
    toObjectGraph.filterNodes(_._2.canMoveThrough).mapNodes(_._1)
}

object BattleMap {
  def create(gf: GridFactory, width: Int, height: Int): BattleMap =
    new BattleMap(gf(width, height, EmptyMapObject))
}
