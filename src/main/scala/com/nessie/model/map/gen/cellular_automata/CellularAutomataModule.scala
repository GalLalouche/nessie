package com.nessie.model.map.gen.cellular_automata

import com.nessie.view.sfx.ScalaFxViewCustomizer
import com.nessie.view.zirconview.ZirconViewCustomizer
import net.codingwell.scalaguice.ScalaModule

object CellularAutomataModule extends ScalaModule {
  override def configure(): Unit = {
    bind[ZirconViewCustomizer] toInstance ZirconCustomizer
    bind[ScalaFxViewCustomizer] toInstance ScalaFxCustomizer
  }
}
