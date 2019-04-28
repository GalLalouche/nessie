package com.nessie.common.collections

import common.AuxSpecs
import org.scalatest.FreeSpec

class UnionFindTest extends FreeSpec with AuxSpecs {
  private val vector = Vector("foo", "bar", "bazz", "quux", "moo")
  private val $ = UnionFind(vector)
  private def deepSets[A](intses: Iterable[Iterable[A]]): Set[Set[A]] = intses.map(_.toSet).toSet
  "all elements begin in different sets" in {
    $.sets shouldReturn vector.map(Set(_))
    $.numberOfSets shouldReturn 5
  }

  "union" - {
    "after union, both items are in the same set" in {
      val unioned = $.union("foo", "bar")
      deepSets(unioned.sets) shouldEqual Set(Set("foo", "bar"), Set("bazz"), Set("quux"), Set("moo"))
      unioned.numberOfSets shouldReturn 4
    }
    "deep union" in {
      val vector = 1 to 10
      val $ = UnionFind(vector).union(1, 2).union(4, 5).union(7, 10).union(4, 7).union(10, 2)
          .union(6, 8).union(6, 9)
          // Pointless joins but they test the numberOfSets
          .union(1, 10).union(5, 7).union(6, 6)
      deepSets($.sets) shouldEqual Set(Set(1, 2, 4, 5, 7, 10), Set(6, 8, 9), Set(3))
      $.numberOfSets shouldReturn 3
    }
  }
}
