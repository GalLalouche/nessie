package com.nessie.model.map.gen

import com.nessie.common.rng.StdGen
import com.nessie.model.map.BattleMap
import com.nessie.model.map.gen.cellular_automata.{CellularAutomataGenerator, ScalaFxCustomizer, ZirconCustomizer}
import com.nessie.view.zirconview.ZirconViewCustomizer
import common.rich.collections.LazyIterable

private object DemoConfigGitIgnore {
  def iterations: LazyIterable[BattleMap] = CellularAutomataGenerator.iterativeGenerator.mkRandom(StdGen(1))
  def scalaFxCustomizer = ScalaFxCustomizer
  def zirconCustomizer: ZirconViewCustomizer = ZirconCustomizer
}
