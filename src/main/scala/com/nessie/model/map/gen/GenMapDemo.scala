package com.nessie.model.map.gen

import com.google.inject.Guice
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.gm.DebugViewFactory
import net.codingwell.scalaguice.InjectorExtensions._

private object GenMapDemo extends ToRngableOps {
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(GenMapDemoModule.module)
    val viewFactory = injector.instance[DebugViewFactory]
    val gen = injector.instance[GenMapStepper]
    viewFactory.create(gen.genStepper)
  }
}
