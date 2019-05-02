package com.nessie.common.graph

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps
import common.rich.RichT._
import common.rich.collections.LazyIterable
import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge

object DfsTraversal {
  private case class Aux[A](graph: Graph[A, UnDiEdge], path: List[A], visited: Set[A]) extends ToRngableOps {
    def head: A = path.head
    def next: Rngable[Option[Aux[A]]] = {
      val head :: tail = path
      if (visited(head)) copy(path = tail).next else {
        val nextNeighbors = graph.get(head).~|.filterNot(visited)
        if (nextNeighbors.isEmpty && tail.isEmpty) Rngable.pure(None) else nextNeighbors.shuffle
            .map(_.map(_.toOuter).toList ++ tail)
            .map(Aux(graph, _, visited + head).opt)
      }
    }
  }

  def apply[A](graph: Graph[A, UnDiEdge], startingPoint: A): Rngable[LazyIterable[A]] = Rngable
      .iterateOptionally(Aux[A](graph, List(startingPoint), Set.empty))(_.next)
      .map(_.map(_.head))
}
