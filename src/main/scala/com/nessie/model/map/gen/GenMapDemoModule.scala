package com.nessie.model.map.gen

import com.google.inject.Module
import com.nessie.common.rng.StdGen
import com.nessie.model.map.gen.cellular_automata.CellularAutomataModule
import com.nessie.model.map.gen.mob.{DemoMobPlacer, MobPlacer}
import com.nessie.view.ViewModule
import net.codingwell.scalaguice.ScalaModule

object GenMapDemoModule {
  def module: Module = new ScalaModule {
    override def configure(): Unit = {
      install(CellularAutomataModule)
      install(ViewModule)
      bind[StdGen] toInstance StdGen(0)
      bind[MobPlacer] toInstance DemoMobPlacer
    }
  }
}
