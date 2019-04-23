package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.common.rng.StdGen
import com.nessie.gm.{GameState, NoOp}
import com.nessie.model.map.BattleMap
import com.nessie.view.sfx.{ScalaFxMapCustomizer, ScalaFxViewCustomizer, ScalaFxViewFactory}

private object GenMapDemo extends ToRngableOps {
  private val Customizer: ScalaFxViewCustomizer = new ScalaFxViewCustomizer {
    override def mapCustomizer = new ScalaFxMapCustomizer {
      override def text = {
        case TunnelMapObject(i) => i.toString
        case RoomMapObject(i) => "R" + i
        case ReachableMapObject(i) => "R" + i
      }
      override def cellColor = {
        case TunnelMapObject(_) => "yellow"
        case ReachableMapObject(_) => "orange"
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val (rooms, gen) = DemoConfigGitIgnore.rooms.random(StdGen.fromSeed(0))
    val (withMazes, gen2) = CreateMazes.go(rooms).random(gen)

    val connected = ConnectRoomsAndMazes.go(withMazes, 0.1).mkRandom(gen2)
    showMap(RemoveDeadEnds(connected))
  }
  private def showMap(map: BattleMap): Unit =
    ScalaFxViewFactory.create(Customizer).updateState(NoOp, GameState.fromMap(map))
}
