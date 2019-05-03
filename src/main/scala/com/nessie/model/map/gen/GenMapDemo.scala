package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.gm.GameState
import com.nessie.model.map.BattleMap
import com.nessie.view.sfx.ScalaFxViewFactory

private object GenMapDemo extends ToRngableOps {
  def main(args: Array[String]): Unit = {
    showMap(DemoConfigGitIgnore.iterations.iterator)
  }
  private def showMap(i: Iterator[BattleMap]): Unit = {
    ScalaFxViewFactory.createWithIterator(DemoConfigGitIgnore.customizer, i map GameState.fromMap)
  }
}
