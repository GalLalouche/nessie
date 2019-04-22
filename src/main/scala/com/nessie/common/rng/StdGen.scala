package com.nessie.common.rng

import scala.util.Random

class StdGen(val seed: Long ) {
  def random: Random = new Random(seed)
  def next: (Long, StdGen) = {
    val nextSeed = random.nextLong()
    nextSeed -> new StdGen(nextSeed)
  }
  def nextSeed: Long = next._1
  def nextGen: StdGen = next._2
  def split: (StdGen, StdGen) = {
    val nextSeed1 = random.nextLong()
    val nextSeed2 = new Random(nextSeed1 + 1).nextLong()
    new StdGen(nextSeed1) -> new StdGen(nextSeed2)
  }
}

object StdGen {
  def fromSeed(seed: Long) = new StdGen(seed)
}