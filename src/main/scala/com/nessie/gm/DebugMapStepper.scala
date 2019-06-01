package com.nessie.gm

import com.nessie.common.rng.Rngable
import com.nessie.model.map.BattleMap
import com.nessie.model.map.gen.{MapIterator, MapIteratorFactory}
import common.rich.collections.LazyIterable
import common.rich.collections.RichList._
import common.rich.collections.RichStream._
import common.rich.primitives.RichBoolean._

import scala.annotation.tailrec

trait DebugMapStepper {
  def currentMap: BattleMap
  def nextSmallStep(): Option[DebugMapStepper]
  def hasNextSmallStep(): Boolean = nextSmallStep().isDefined
  def finishCurrentStep(): DebugMapStepper = LazyIterable.iterateOptionally(this)(_.nextSmallStep()).last
  def nextBigStep(): Option[DebugMapStepper]
  def hasNextBigStep(): Boolean = nextBigStep().isDefined
  @tailrec
  private def finishAllAux(stepper: DebugMapStepper): DebugMapStepper =
    if (stepper.hasNextSmallStep().isFalse && stepper.hasNextBigStep().isFalse)
      stepper
    else
      finishAllAux(if (stepper.hasNextSmallStep()) stepper.finishCurrentStep() else stepper.nextBigStep().get)
  def finishAll(): DebugMapStepper = finishAllAux(this)
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
  def from(iterator: MapIterator): Rngable[DebugMapStepper] =
    iterator.steps.map(_.toStream).map(new StreamStepper(_, iterator.canonize))
  def from(factory: MapIteratorFactory): Rngable[DebugMapStepperFactory] =
    Rngable.fromStdGen(stdGen => gs => from(factory.generate(gs)).mkRandom(stdGen))

  def Null: DebugMapStepper = new DebugMapStepper {
    override def currentMap = throw new UnsupportedOperationException
    override def nextSmallStep() = None
    override def nextBigStep() = None
    override def canonize = throw new UnsupportedOperationException
  }

  private class Composite(currentStepper: DebugMapStepper, steppers: List[BattleMap => DebugMapStepper])
      extends DebugMapStepper {
    override def currentMap: BattleMap = currentStepper.currentMap
    private def currentOption(f: DebugMapStepper => Option[DebugMapStepper]): Option[DebugMapStepper] =
      f(currentStepper).map(new Composite(_, steppers))
    override def nextSmallStep(): Option[DebugMapStepper] = currentOption(_.nextSmallStep())
    override def hasNextSmallStep(): Boolean = currentStepper.hasNextSmallStep()
    override def nextBigStep(): Option[DebugMapStepper] = currentOption(_.nextBigStep())
        .orElse(steppers.headTailOption
            .map {case (head, tail) => new Composite(head(currentStepper.canonize), tail)})
    override def hasNextBigStep() = currentStepper.hasNextBigStep() || steppers.nonEmpty
    override def canonize = currentStepper.canonize
  }

  def composite(
      stepper1: DebugMapStepperFactory,
      steppers: (BattleMap => DebugMapStepper)*,
  ): DebugMapStepperFactory = gridSize => new Composite(stepper1(gridSize), steppers.toList)
}
