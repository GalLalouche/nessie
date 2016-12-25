package com.nessie.common.graph

import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge

object RichGraphs {
  implicit def richUndirected[T](g: Graph[T, UnDiEdge]): RichUndirected[T] = new RichUndirected(g)
}
