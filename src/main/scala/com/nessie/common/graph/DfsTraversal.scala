package com.nessie.common.graph

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.{RngableIterable, RngableOption}
import com.nessie.common.rng.Rngable.ToRngableOps._
import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge

import scalaz.OptionT

import common.rich.RichT._

object DfsTraversal {
  private case class Aux[A: Ordering](graph: Graph[A, UnDiEdge], path: List[A], visited: Set[A]) {
    def head: A = path.head
    def next: RngableOption[Aux[A]] = {
      val head :: tail = path
      if (visited(head)) copy(path = tail).next else {
        // Sorting is required for reproducibility.
        val nextNeighbors = graph.get(head).~|.filterNot(visited).view.map(_.toOuter).toVector.sorted
        if (nextNeighbors.isEmpty && tail.isEmpty) Rngable.none else nextNeighbors.shuffle
            .map(_.toList ++ tail)
            .map(Aux(graph, _, visited + head).opt) |> OptionT.apply
      }
    }
  }

  // A: Ordering is required for reproducibility.
  def apply[A: Ordering](graph: Graph[A, UnDiEdge], startingPoint: A): RngableIterable[A] = Rngable
      .iterateOptionally(Aux[A](graph, List(startingPoint), Set.empty))(_.next)
      .map(_.head)
}
