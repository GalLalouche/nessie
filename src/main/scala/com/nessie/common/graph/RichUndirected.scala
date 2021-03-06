package com.nessie.common.graph

import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge

import scala.annotation.tailrec
import scala.collection.immutable.Queue

object RichUndirected {
  implicit class richUndirected[A]($: Graph[A, UnDiEdge]) {
    private def neighbors(t: A): Traversable[A] = $.get(t).diSuccessors.map(_.value)

    private def distances(source: A, destination: Option[A], maxDistance: Option[Int]): Map[A, Int] = {
      require($.nodes.contains(source), "No node equal to " + source)
      destination.foreach(d => require($.nodes.contains(d), "No node equal to " + d))

      @tailrec
      def aux(queue: Queue[(A, Int)], result: Map[A, Int]): Map[A, Int] = queue.dequeueOption match {
        case None => result
        case Some(((next, d), tail)) =>
          if (result.contains(next) || maxDistance.exists(_ < d))
            aux(tail, result)
          else {
            val nextResult = result + (next -> d)
            if (destination contains next)
              nextResult
            else
              aux(tail ++ neighbors(next).filterNot(result.contains).map(e => e -> (d + 1)), nextResult)
          }
      }
      aux(Queue(source -> 0), Map())
    }

    def distance(source: A, destination: A): Option[Int] =
      distances(source, destination = Some(destination), maxDistance = None).get(destination)
    def areConnected(source: A, destination: A): Boolean = distance(source, destination).isDefined
    def distances(source: A): Map[A, Int] = distances(source, destination = None, maxDistance = None)
    def distances(source: A, maxDistance: Int): Map[A, Int] =
      distances(source, destination = None, maxDistance = Some(maxDistance))

    def outerNodes: Iterable[A] = $.nodes
    def outerEdges: Iterable[UnDiEdge[A]] = $.edges

    def mapNodes[B](f: A => B): Graph[B, UnDiEdge] = {
      val mappedNodes = outerNodes.map(f)
      val mappedEdges = outerEdges.map {case UnDiEdge(a, b) => UnDiEdge(f(a), f(b))}
      Graph.from(mappedNodes, mappedEdges)
    }

    def removeNodes(xs: Traversable[A]): Graph[A, UnDiEdge] = $ -- Graph.from(xs, Nil)
    def filterNodes(p: A => Boolean): Graph[A, UnDiEdge] =
      $ -- Graph.from(outerNodes.filterNot(p), Nil)

    def stronglyConnectedComponents: Iterable[Iterable[A]] =
      $.strongComponentTraverser().map(_.nodes.map(_.toOuter)).toIterable
  }
}
