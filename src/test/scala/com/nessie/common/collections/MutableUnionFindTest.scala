package com.nessie.common.collections

import common.AuxSpecs
import org.scalatest.{FreeSpec, OneInstancePerTest}

// TODO handle code duplication with the immutable version test
class MutableUnionFindTest extends FreeSpec with AuxSpecs with OneInstancePerTest {
  private val vector = Vector("foo", "bar", "bazz", "quux", "moo")
  private val $ = MutableUnionFind(vector)
  private def deepSets[A](intses: Iterable[Iterable[A]]): Set[Set[A]] = intses.map(_.toSet).toSet
  "all elements begin in different sets" in {
    $.sets shouldReturn vector.map(Set(_))
  }

  "union" - {
    "after union, both items are in the same set" in {
      $.union("foo", "bar")
      deepSets($.sets) shouldEqual Set(Set("foo", "bar"), Set("bazz"), Set("quux"), Set("moo"))
      $.numberOfSets shouldReturn 4
    }
    "deep union" in {
      val vector = 1 to 10
      val $ = MutableUnionFind(vector)
      $.union(1, 2)
      $.union(4, 5)
      $.union(7, 10)
      $.union(4, 7)
      $.union(10, 2)
      $.union(6, 8)
      $.union(6, 9)
      // Pointless joins but they test the numberOfSets
      $.union(1, 10)
      $.union(5, 7)
      $.union(6, 6)
      deepSets($.sets) shouldEqual Set(Set(1, 2, 4, 5, 7, 10), Set(6, 8, 9), Set(3))
      $.numberOfSets shouldReturn 3
    }
  }
}
