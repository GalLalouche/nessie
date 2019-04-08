package com.nessie.common.graph

import scala.annotation.tailrec
import scala.collection.immutable.Queue
import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge

object RichUndirected {
  implicit class richUndirected[T](g: Graph[T, UnDiEdge]) {
    private def neighbors(t: T): Traversable[T] = g.get(t).diSuccessors.map(_.value)

    private def distances(source: T, destination: Option[T]): Map[T, Int] = {
      require(g.nodes.contains(source), "No node equal to " + source)
      destination.foreach(d => require(g.nodes.contains(d), "No node equal to " + d))

      @tailrec
      def aux(queue: Queue[(T, Int)], result: Map[T, Int]): Map[T, Int] = queue.dequeueOption match {
        case None => result
        case Some(((next, d), tail)) =>
          if (result.contains(next))
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

    def distance(source: T, destination: T): Option[Int] = distances(source, Some(destination)).get(destination)

    def distances(source: T): Map[T, Int] = distances(source, None)
    // TODO optimize
    def distances(source: T, maxDistance: Int): Map[T, Int] = distances(source, None).filter(_._2 <= maxDistance)
  }
}
