package com.nessie.model.map.gen

import com.nessie.common.rng.Rngable.ToRngableOps
import com.nessie.gm.{GameState, NoOp}
import com.nessie.model.map.BattleMap
import com.nessie.view.sfx.{ScalaFxMapCustomizer, ScalaFxViewCustomizer, ScalaFxViewFactory}

private object GenMapDemo extends ToRngableOps {
  private val Customizer: ScalaFxViewCustomizer = new ScalaFxViewCustomizer {
    override def mapCustomizer = new ScalaFxMapCustomizer {
      override def text = {
        case TunnelMapObject(i) => i.toString
        case RoomMapObject(i) => "R" + i
        case ReachableMapObject(o, i) => o match {
          case RoomMapObject(i2) => s"R$i2,$i"
          case TunnelMapObject(i2) => s"T$i2,$i"
        }
      }
      override def cellColor = {
        case TunnelMapObject(_) => "yellow"
        case ReachableMapObject(o, _) => o match {
          case _: TunnelMapObject => "orange"
          case _: RoomMapObject => "cyan"
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val (rooms, gen) = DemoConfigGitIgnore.rooms
    println(gen)
    val finalMap = (for {
      connected <- ConnectRoomsViaPairs.iterate(rooms)
      //connected <- ConnectRoomsAndMazes.go(withMazes, 0.1)
    } yield connected)
            .mkRandom(gen)
    showMap(finalMap.iterator)
  }
  private def showMap(i: Iterator[BattleMap]): Unit = {
    ScalaFxViewFactory.createWithIterator(Customizer, i map GameState.fromMap)
  }
}
