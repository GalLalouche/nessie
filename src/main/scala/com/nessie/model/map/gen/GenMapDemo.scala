package com.nessie.model.map.gen

import com.google.inject.Guice
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.gm.{DebugMapStepper, DebugViewFactory}
import net.codingwell.scalaguice.InjectorExtensions._

private object GenMapDemo extends ToRngableOps {
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(GenMapDemoModule)
    injector.instance[DebugViewFactory] create injector.instance[DebugMapStepper]
  }
}
