package com.nessie.model.map.gen

import com.nessie.common.rng.StdGen
import com.nessie.model.map.BattleMap
import com.nessie.model.map.gen.rooms_to_mazes.{RoomsThenMazesGenerator, ScalaFxCustomizer}

private object DemoConfigGitIgnore {
  def generateMap: BattleMap = RoomsThenMazesGenerator.generator.mkRandom(StdGen(0))
  def customizer = ScalaFxCustomizer
}
