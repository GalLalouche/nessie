package com.nessie.model.map.gen

import com.nessie.common.rng.StdGen
import com.nessie.gm.DebugMapStepper
import com.nessie.model.map.gen.mob.MobPlacer
import com.nessie.model.map.BattleMap
import common.rich.RichT._
import javax.inject.Inject

private class GenMapStepper @Inject()(
    mapGenerator: MapGenerator,
    mobPlacer: MobPlacer,
    stdGen: StdGen
) {
  private val (s1, s2) = stdGen.split
  class FirstStepper(s: Stream[BattleMap]) extends DebugMapStepper {
    override def currentMap: BattleMap = s.head
    override def nextSmallStep(): Option[DebugMapStepper] =
      s.tail.opt.filter(_.nonEmpty).map(new FirstStepper(_))
    override def nextBigStep(): Option[DebugMapStepper] =
      Some(new SecondStepper(mapGenerator.canonize(currentMap)))
    override def canonize: BattleMap = mapGenerator.canonize(currentMap)
  }
  class SecondStepper(map: BattleMap) extends DebugMapStepper {
    override def currentMap: BattleMap = mobPlacer.place(map).mkRandom(s2)
    override def nextSmallStep(): Option[DebugMapStepper] = None
    override def nextBigStep(): Option[DebugMapStepper] = None
    override def canonize: BattleMap = currentMap
  }
  def genStepper: DebugMapStepper =
    new FirstStepper(mapGenerator.iterativeGenerator.map(_.toStream).mkRandom(s1))
}

private object GenMapStepper {
}
