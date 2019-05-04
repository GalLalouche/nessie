package com.nessie.common.rng

import scala.util.Random

case class StdGen(seed: Long) {
  def random: Random = new Random(seed)
  def next: (Long, StdGen) = {
    // Avoid returning a seed that clients might accidentally generate themselves.
    val s1 = new Random(random.nextLong()).nextLong()
    val s2 = -new Random(-random.nextLong()).nextLong()
    val nextSeed = s1 ^ s2
    nextSeed -> StdGen(nextSeed)
  }
  def nextSeed: Long = next._1
  def nextGen: StdGen = next._2
  def split: (StdGen, StdGen) = StdGen(seed - 1).next._2 -> StdGen(seed + 1).next._2
}

object StdGen {
  def fromSeed(seed: Long) = StdGen(seed)
}