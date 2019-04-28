package com.nessie.common.collections

import common.rich.func.MoreIterableInstances

import scala.annotation.tailrec

import scalaz.syntax.ToFunctorOps
import common.rich.collections.RichSeq._

/**
 * An immutable implementation of the UnionFind (Disjoint Set) algorithm. Since it's immutable, path
 * shortening (which actually ensures O(log*(n)) performance) can only be done on unions, but on sameSet
 * checks. In other words, this could mean that the actual performance would be O(n) in the worst case for
 * some specific union operations.
 * See MutableUnionFind for the by-the-book implementation and performance.
 */
// TODO move to ScalaCommon
class UnionFind[A] private(parents: Vector[Int], index: Map[A, Int], val numberOfSets: Int)
    extends ToFunctorOps with MoreIterableInstances {
  def contains(a: A): Boolean = index.contains(a)
  /**
   * Returns inverse path to the parent, i.e., the first element in the list is the parent and the last
   * element is a. This is needed later for path shortening.
   */
  private def getPathToParent(a: A): List[Int] = {
    @tailrec
    def aux(id: Int, result: List[Int]): List[Int] = {
      val parent = parents(id)
      if (parent == id) parent :: result else aux(parent, parent :: result)
    }
    aux(index(a), Nil)
  }
  private def getSet(a: A): Int = getPathToParent(a).head
  def sameSet(a1: A, a2: A): Boolean = getSet(a1) == getSet(a2)
  def union(a1: A, a2: A): UnionFind[A] = {
    val paths1 = getPathToParent(a1)
    val paths2 = getPathToParent(a2)
    // We don't early exit if a1 and a2 are already in the same set since we might apply shortening here.
    val sameSet = paths1.head == paths2.head
    val destination = paths1.head
    // Shorten the paths by setting the set index of all indices in the path to the same parent.
    val shortened = paths1.iterator.++(paths2.iterator)./:(parents)(_.updated(_, destination))
    new UnionFind[A](shortened, index, numberOfSets - (if (sameSet) 0 else 1))
  }

  /**
   * Technically it's a set of sets, but this is a more efficient implementation since it doesn't require
   * comparing and hashing entire sets.
   */
  def sets: Iterable[Iterable[A]] = index.keys.fproduct(getSet).groupBy(_._2).values.map(_.map(_._1))
  def values: Iterable[A] = index.keys
}

object UnionFind {
  def apply[A](a1: A, a2: A, as: A*): UnionFind[A] = apply(a2 :: a1 :: as.toList)
  def apply[A](as: TraversableOnce[A]): UnionFind[A] = {
    val vector = as.toVector
    new UnionFind[A](vector.indices.toVector, vector.zipWithIndex.toMap, vector.size)
  }
}
