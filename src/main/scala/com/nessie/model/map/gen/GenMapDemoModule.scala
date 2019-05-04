package com.nessie.model.map.gen

import com.google.inject.Module
import com.nessie.common.rng.StdGen
import com.nessie.gm.IterativeViewFactory
import com.nessie.model.map.gen.cellular_automata.{CellularAutomataGenerator, CellularAutomataModule}
import com.nessie.view.zirconview.ZirconViewFactory
import net.codingwell.scalaguice.ScalaModule

object GenMapDemoModule {
  def module: Module = new ScalaModule {
    override def configure(): Unit = {
      install(CellularAutomataModule)
      bind[StdGen] toInstance StdGen(0)
      bind[MapGenerator] toInstance CellularAutomataGenerator
      bind[IterativeViewFactory].to[ZirconViewFactory]
    }
  }
}
