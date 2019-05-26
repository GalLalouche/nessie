package com.nessie.gm

import com.nessie.common.rng.StdGen
import com.nessie.model.map.BattleMap
import com.nessie.model.map.gen.MapGenerator
import common.rich.collections.LazyIterable
import common.rich.collections.RichStream._

trait DebugMapStepper {
  def currentMap: BattleMap
  // TODO has hasNextSmallStep, since calling next and checking if it's defined can be pretty expensive.
  def nextSmallStep(): Option[DebugMapStepper]
  def finishCurrentStep(): DebugMapStepper = LazyIterable.iterateOptionally(this)(_.nextSmallStep()).last
  def nextBigStep(): Option[DebugMapStepper]
  /** Remove any internal map objects and replace them with the canonical ones, e.g., FullWall and EmptyMapObject. */
  def canonize: BattleMap
}

object DebugMapStepper {
  private class StreamStepper(
      s: Stream[BattleMap], canonizer: BattleMap => BattleMap) extends DebugMapStepper {
    override def currentMap: BattleMap = s.head
    override def nextSmallStep(): Option[DebugMapStepper] = s.tailOption.map(new StreamStepper(_, canonizer))
    override def nextBigStep(): Option[DebugMapStepper] = None
    override def canonize: BattleMap = canonizer(currentMap)
  }
  def from(mg: MapGenerator, stdGen: StdGen): DebugMapStepper = new StreamStepper(
    mg.iterativeGenerator.map(_.toStream).mkRandom(stdGen),
    mg.canonize
  )

  def Null: DebugMapStepper = new DebugMapStepper {
    override def currentMap = ???
    override def nextSmallStep() = None
    override def nextBigStep() = None
    override def canonize = ???
  }

  private class Composite(currentStepper: DebugMapStepper, steppers: List[BattleMap => DebugMapStepper])
      extends DebugMapStepper {
    override def currentMap: BattleMap = currentStepper.currentMap
    private def currentOption(f: DebugMapStepper => Option[DebugMapStepper]): Option[DebugMapStepper] =
      f(currentStepper).map(new Composite(_, steppers))
    override def nextSmallStep(): Option[DebugMapStepper] = currentOption(_.nextSmallStep())
    override def nextBigStep(): Option[DebugMapStepper] = currentOption(_.nextBigStep()).orElse(
      steppers match { // TODO extract head :: tail opt
        case Nil => None
        case head :: tail => Some(new Composite(head(currentMap), tail))
      })
    override def canonize = currentStepper.canonize
  }

  def composite(stepper1: DebugMapStepper, steppers: (BattleMap => DebugMapStepper)*): DebugMapStepper =
    new Composite(stepper1, steppers.toList)
}
