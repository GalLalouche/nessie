package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.common.rng.StdGen
import com.nessie.gm.{GameState, NoOp}
import com.nessie.view.sfx.{ScalaFxMapCustomizer, ScalaFxViewCustomizer, ScalaFxViewFactory}

private object GenMapDemo extends ToRngableOps {
  val Customizer: ScalaFxViewCustomizer = new ScalaFxViewCustomizer {
    override def mapCustomizer = new ScalaFxMapCustomizer {
      override def text = {
        case TunnelMapObject(i) => i.toString
        case RoomMapObject(i) => "R" + i
      }
      override def cellColor = {
        case TunnelMapObject(_) => "yellow"
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val (rooms, gen) = DemoConfigGitIgnore.rooms.random(StdGen.fromSeed(9))
    //ScalaFxViewFactory.create().updateState(NoOp, GameState.fromMap(rooms.map))
    val withMazes = CreateMazes.go(rooms).mkRandom(gen)
    val gs = GameState.fromMap(withMazes)
    ScalaFxViewFactory.create(Customizer).updateState(NoOp, gs)
  }
}
