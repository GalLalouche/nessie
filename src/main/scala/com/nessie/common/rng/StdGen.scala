package com.nessie.common.rng

import scala.util.Random

case class StdGen(seed: Long) {
  def random: Random = new Random(seed)
  def next: (Long, StdGen) = {
    val nextSeed = random.nextLong()
    nextSeed -> StdGen(nextSeed)
  }
  def nextSeed: Long = next._1
  def nextGen: StdGen = next._2
  def split: (StdGen, StdGen) = {
    val nextSeed1 = random.nextLong()
    val nextSeed2 = new Random(nextSeed1 + 1).nextLong()
    StdGen(nextSeed1) -> StdGen(nextSeed2)
  }
}

object StdGen {
  def fromSeed(seed: Long) = StdGen(seed)
}