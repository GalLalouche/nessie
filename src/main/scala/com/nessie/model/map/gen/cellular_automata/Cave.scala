package com.nessie.model.map.gen.cellular_automata

import com.nessie.model.map.{BattleMap, MapPoint}
import common.rich.primitives.RichBoolean._
import common.uf.ImmutableUnionFind

private case class Cave(mapPoints: Seq[MapPoint], id: Char) {
  private val set = mapPoints.toSet
  def contains(mp: MapPoint) = set(mp)
  override def toString = s"Cave@$id"
}
private object Cave {
  implicit val CaveOrdering: Ordering[Cave] = Ordering.by(_.id)
}
private case class Caves(caves: Seq[Cave], uf: ImmutableUnionFind[Cave]) {
  // TODO implement more efficiently
  def cave(mp: MapPoint): Option[Cave] = caves.find(_.contains(mp))
  def isConnected = uf.hasSingleSet
  def areConnected(c1: Cave, c2: Cave): Boolean = uf.sameSet(c1, c2)
  def mark(map: BattleMap) =
    map.foldPoints((map, p) => cave(p).map(CaveMapObject apply _.id).fold(map)(map.replaceSafely(p, _)))
}
private object Caves {
  private val IDs = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
  def from(map: BattleMap): Caves = {
    import com.nessie.common.graph.RichUndirected._
    val cavePoints = map.toPointGraph
        .removeNodes(map.objects.filter(_._2.canMoveThrough.isFalse).map(_._1))
        .stronglyConnectedComponents
        .view.map(_.toVector.sorted)
        .toVector
        .sortBy(_.min)
    val caves = cavePoints.zip(IDs).map(Function tupled Cave.apply)
    Caves(caves, ImmutableUnionFind(caves))
  }
}

