package com.nessie.common.graph

import com.nessie.common.graph.RichUndirected._
import org.scalatest.{FreeSpec, OneInstancePerTest}
import org.scalatest.concurrent.{Signaler, TimeLimitedTests}
import org.scalatest.time.Span
import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.GraphPredef._
import scalax.collection.immutable.Graph

import scala.concurrent.duration.DurationInt

import common.rich.RichTuple._
import common.rich.collections.RichTraversableOnce._
import common.test.AuxSpecs

class RichUndirectedTest extends FreeSpec with AuxSpecs with TimeLimitedTests with OneInstancePerTest {
  override def timeLimit = Span.convertDurationToSpan(1.second)
  override val defaultTestSignaler = Signaler(_.stop())
  "distance" - {
    "Throws exception if missing source" in {
      an[IllegalArgumentException] should be thrownBy Graph[Int, UnDiEdge](1, 2, 3).distance(1, 4)
      an[IllegalArgumentException] should be thrownBy Graph[Int, UnDiEdge](1, 2, 3).distance(4, 1)
    }
    "source is equal to destination" in {
      Graph[Int, UnDiEdge](1).distance(1, 1).get shouldReturn 0
    }
    "No path" in {
      Graph[Int, UnDiEdge](1, 2).distance(1, 2) shouldReturn None
    }
    "Simplest" in {
      Graph[Int, UnDiEdge](1, 2, 1 ~ 2).distance(1, 2).get shouldReturn 1
    }
    "Simpler" in {
      Graph[Int, UnDiEdge](1, 2, 1 ~ 2, 2 ~ 3).distance(1, 3).get shouldReturn 2
    }
    "Diamond" in {
      Graph[Int, UnDiEdge](1, 2, 3, 4, 1 ~ 2, 1 ~ 3, 2 ~ 4, 3 ~ 4).distance(1, 4).get shouldReturn 2
    }
    "Cycles" - {
      "Self" - {
        "Distance to self is still 0" in {
          Graph[Int, UnDiEdge](1, 1 ~ 1).distance(1, 1).get shouldReturn 0
        }
        "Distance to other is correct" in {
          Graph[Int, UnDiEdge](1, 1 ~ 1, 2, 3, 1 ~ 2, 2 ~ 2, 2 ~ 3, 3 ~ 3)
              .distance(1, 3).get shouldReturn 2
        }
      }
      "Other" - {
        "1" in {
          Graph[Int, UnDiEdge](1, 2, 3, 4, 5, 1 ~ 2, 2 ~ 3, 3 ~ 4, 4 ~ 2, 4 ~ 5)
              .distance(1, 5).get shouldReturn 3
        }
        "2" in {
          Graph[Int, UnDiEdge](1, 2, 3, 1 ~ 2, 2 ~ 3, 3 ~ 1).distances(1) shouldReturn
              Map(2 -> 1, 3 -> 1, 1 -> 0)
        }
        "3" in {
          val vertices = 1 to 100
          val edges = vertices.unorderedPairs.map(_.reduce(_ ~ _)).toVector
          Graph.from(vertices, edges).distances(1) shouldReturn (Map(1 -> 0) ++ (2 to 100).map(_ -> 1).toMap)
        }
      }
    }
    "maxDistance" in {
      val vertices = 1 to 100
      val edges = vertices.sliding(2).map(e => e(0) ~ e(1)).toVector
      Graph.from(vertices, edges).distances(1, 3) shouldReturn Map(1 -> 0, 2 -> 1, 3 -> 2, 4 -> 3)
    }
  }
  "mapNodes" in {
    Graph("foo" ~ "bazz", "bazz" ~ "foobar").mapNodes(_.length) shouldReturn Graph(3 ~ 4, 4 ~ 6)
  }
  "removeNodes" in {
    Graph("foo" ~ "bazz", "bazz" ~ "foobar").removeNodes(Set("foo")) shouldReturn Graph("bazz" ~ "foobar", "bazz")
  }
}
