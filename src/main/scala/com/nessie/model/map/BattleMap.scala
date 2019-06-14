package com.nessie.model.map

import com.nessie.common.graph.RichUndirected._
import com.nessie.model.units.Owner
import common.rich.func.MoreIteratorInstances._
import common.rich.func.ToMoreMonadPlusOps._
import common.rich.RichT._
import common.rich.RichTuple._
import common.rich.primitives.RichBoolean._
import monocle.macros.Lenses
import monocle.Lens
import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge

/** A map of a given level without between-objects, so walls and its ilks taken up a full tile. */
@Lenses
case class BattleMap private(grid: Grid[BattleMapObject])
    extends GridLike[BattleMap, BattleMapObject] {
  def owners: Set[Owner] = objects.iterator.map(_._2).select[CombatUnitObject].map(_.unit.owner).toSet

  override protected val gridLens: Lens[BattleMap, Grid[BattleMapObject]] = BattleMap.grid

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
  def create(gf: GridFactory, gridSize: GridSize): BattleMap = new BattleMap(gf(gridSize, EmptyMapObject))
  def create(gf: GridFactory, width: Int, height: Int): BattleMap =
    new BattleMap(gf(GridSize(width = width, height = height), EmptyMapObject))
}
