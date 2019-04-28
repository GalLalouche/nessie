package com.nessie.common.collections

import common.rich.func.MoreIterableInstances

import scala.annotation.tailrec

import scalaz.syntax.ToFunctorOps

/**
 * A mutable implementation of the UnionFind (Disjoint Set) algorithm. Since it's mutable, path
 * shortening can be done on unions *and* sameSet queries, so it has true O(log*(n)) amortized performance.
 * This class is *not* thread-safe!
 */
// TODO move to ScalaCommon
class MutableUnionFind[A] private(parents: Array[Int], index: Map[A, Int], private var _numberOfSets: Int)
    extends ToFunctorOps with MoreIterableInstances {
  /** Sets the parent of all the indices in the iterator to that of the head of the iterator. */
  private def shorten(i: Iterator[Int]): Unit = {
    val next = i.next
    i.foreach(parents(_) = next)
  }

  def contains(a: A): Boolean = index.contains(a)
  /**
   * Returns inverse path to the parent, i.e., the first element in the list is the parent and the last
   * element is a. This is needed later for path shortening.
   */
  // TODO handle code duplication with the immutable version.
  private def getPathToParent(a: A): List[Int] = {
    @tailrec
    def aux(id: Int, result: List[Int]): List[Int] = {
      val parent = parents(id)
      if (parent == id) parent :: result else aux(parent, parent :: result)
    }
    aux(index(a), Nil)
  }
  private def getSet(a: A): Int = {
    val list = getPathToParent(a)
    shorten(list.iterator)
    list.head
  }
  def sameSet(a1: A, a2: A): Boolean = getSet(a1) == getSet(a2)
  def union(a1: A, a2: A): Unit = {
    if (sameSet(a1, a2))
      return // Same set already shortens, so there's nothing left to do
    shorten(getPathToParent(a1).iterator ++ getPathToParent(a2).iterator)
    _numberOfSets -= 1
  }

  def numberOfSets: Int = _numberOfSets

  /**
   * Technically it's a set of sets, but this is a more efficient implementation since it doesn't require
   * comparing and hashing entire sets.
   */
  // TODO handle code duplication with the immutable version.
  def sets: Iterable[Iterable[A]] = index.keys.fproduct(getSet).groupBy(_._2).values.map(_.map(_._1))
  def values: Iterable[A] = index.keys
}

object MutableUnionFind {
  def apply[A](a1: A, a2: A, as: A*): MutableUnionFind[A] = apply(a2 :: a1 :: as.toList)
  def apply[A](as: TraversableOnce[A]): MutableUnionFind[A] = {
    val vector = as.toVector
    new MutableUnionFind[A](vector.indices.toArray, vector.zipWithIndex.toMap, vector.size)
  }
}
