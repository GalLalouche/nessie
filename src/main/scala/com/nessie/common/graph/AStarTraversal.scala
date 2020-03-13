package com.nessie.common.graph

import com.nessie.common.rng.Rngable
import com.nessie.common.rng.Rngable.ToRngableOps._
import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge

import common.rich.RichT._
import common.rich.collections.LazyIterable

object AStarTraversal {
  import com.nessie.common.graph.Metric.Implicits._

  private case class Aux[A: Ordering : Metric](
      graph: Graph[A, UnDiEdge], path: List[A], visited: Set[A], destination: A,
  ) {
    def head: A = path.head
    def next: Rngable[Option[Aux[A]]] = {
      val head :: tail = path
      if (head == destination) Rngable.pure(None) else if (visited(head)) copy(path = tail).next else {
        val nextNeighbors = graph.get(head)
            .~|
            .view
            .filterNot(visited)
        if (nextNeighbors.isEmpty && tail.isEmpty) Rngable.pure(None) else nextNeighbors
            .map(_.toOuter)
            .groupBy(_.distanceTo(destination))
            .minBy(_._1)
            ._2
            .toVector
            .sorted // Sorting is required to ensure deterministic results.
            .shuffle
            .map(_.toList ++ tail)
            .map(Aux(graph, _, visited + head, destination).opt)
      }
    }
  }

  // A: Ordering is required for reproducibility.
  // Rngable is needed for choosing between two options with the same distance
  def apply[A: Ordering : Metric](graph: Graph[A, UnDiEdge], startingPoint: A, target: A): Rngable[LazyIterable[A]] =
    Rngable
        .iterateOptionally(Aux[A](graph, List(startingPoint), Set.empty, target))(_.next)
        .map(_.map(_.head))
}
