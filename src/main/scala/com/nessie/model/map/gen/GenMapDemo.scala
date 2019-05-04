package com.nessie.model.map.gen

import com.google.inject.Guice
import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.common.rng.StdGen
import com.nessie.gm.{GameState, IterativeViewFactory}
import net.codingwell.scalaguice.InjectorExtensions._

private object GenMapDemo extends ToRngableOps {
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(GenMapDemoModule.module)
    val generator = injector.instance[MapGenerator]
    val viewFactory = injector.instance[IterativeViewFactory]
    val gen = injector.instance[StdGen]
    viewFactory.create(generator.iterativeGenerator.mkRandom(gen).map(GameState.fromMap).iterator)
  }
}
