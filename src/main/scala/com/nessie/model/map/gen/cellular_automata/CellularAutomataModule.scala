package com.nessie.model.map.gen.cellular_automata

import com.google.inject.Provides
import com.nessie.common.rng.StdGen
import com.nessie.gm.DebugMapStepper
import com.nessie.model.map.gen.MapGenerator
import com.nessie.view.sfx.ScalaFxViewCustomizer
import com.nessie.view.zirconview.ZirconViewCustomizer
import net.codingwell.scalaguice.ScalaModule

object CellularAutomataModule extends ScalaModule {
  override def configure(): Unit = {
    bind[ZirconViewCustomizer] toInstance ZirconCustomizer
    bind[ScalaFxViewCustomizer] toInstance ScalaFxCustomizer
    bind[MapGenerator] toInstance CellularAutomataGenerator
  }

  @Provides private def provideStepper(stdGen: StdGen): DebugMapStepper = {
    val (s1, s2) = stdGen.split
    DebugMapStepper.composite(
      DebugMapStepper.from(CellularAutomataGenerator, s1),
      (CellularDigger.generator _).andThen(DebugMapStepper.from(_, s2)),
    )
  }
}
