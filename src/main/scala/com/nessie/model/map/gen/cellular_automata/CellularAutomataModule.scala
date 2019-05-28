package com.nessie.model.map.gen.cellular_automata

import com.google.inject.Provides
import com.nessie.common.rng.StdGen
import com.nessie.gm.{DebugMapStepper, DebugMapStepperFactory}
import com.nessie.model.map.gen.MapIteratorFactory
import com.nessie.view.sfx.ScalaFxViewCustomizer
import com.nessie.view.zirconview.ZirconViewCustomizer
import common.rich.RichT._
import net.codingwell.scalaguice.ScalaModule

object CellularAutomataModule extends ScalaModule {
  override def configure(): Unit = {
    bind[ZirconViewCustomizer] toInstance ZirconCustomizer
    bind[ScalaFxViewCustomizer] toInstance ScalaFxCustomizer
    bind[MapIteratorFactory] toInstance CellularAutomataGenerator
  }

  @Provides private def provideStepper(stdGen: StdGen): DebugMapStepperFactory = {
    val (s1, s2) = stdGen.split
    DebugMapStepper.composite(
      DebugMapStepper.from(CellularAutomataGenerator).mkRandom(s1),
      CellularDigger.iterator(_).mapTo(DebugMapStepper.from).mkRandom(s2),
    )
  }
}
