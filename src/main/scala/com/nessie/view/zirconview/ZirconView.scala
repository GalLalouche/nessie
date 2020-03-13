package com.nessie.view.zirconview

import ch.qos.logback.classic.Level
import com.nessie.gm.{GameLoop, GameState, GameStateChange, PlayerInput, View}
import com.nessie.model.map.Direction
import com.nessie.model.units.{CombatUnit, Owner}
import com.nessie.view.zirconview.input.ZirconPlayerInput
import com.nessie.view.zirconview.ZirconUtils._
import com.nessie.view.zirconview.screen.{DebugButton, ZirconScreen}
import com.nessie.view.zirconview.ZirconView._
import com.nessie.view.MapAndPlayerFog
import org.hexworks.zircon.api.uievent.KeyCode
import org.slf4j.LoggerFactory

import scalaz.std.option.optionInstance
import scalaz.syntax.functor.ToFunctorOps
import common.rich.func.MoreObservableInstances._
import common.rich.func.ToMoreMonadPlusOps._

import common.rich.RichT._

private class ZirconView(screen: ZirconScreen) extends View {
  LoggerFactory.getLogger("org.hexworks").asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.WARN)

  override def updateState(change: GameStateChange, state: GameState): Unit = synchronized {
    screen.updateMap(MapAndPlayerFog(state.map, state.mapAndFogs.fogsForOwner(Owner.Player)))
  }

  screen.screen.keyboardActions().oMap(ke => ArrowKeys.get(ke.getCode).strengthL(ke)).foreach {case (ke, d) =>
    screen.map.scroll(n = if (ke.getCtrlDown) 10 else if (ke.getShiftDown) 5 else 1, direction = d)
    screen.drawMap()
  }
  screen.debugButtons
      .select[DebugButton.FinishAll].head
      .map(_.map |> GameState.fromMap)
      .foreach(GameLoop.initialize(this, _))
  override def playerInput = new PlayerInput {
    override def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState) =
      new ZirconPlayerInput(
        screen,
        screen.map,
        screen.instructions,
        screenDrawer = screen.drawMap,
        highlighter = screen.highlighter,
      ).nextState(currentlyPlayingUnit, gs)
  }

  def nextSmallStep(): Unit = screen.nextSmallStep()
}

private object ZirconView {
  private val ArrowKeys: Map[KeyCode, Direction] = Map(
    KeyCode.UP -> Direction.Up,
    KeyCode.DOWN -> Direction.Down,
    KeyCode.LEFT -> Direction.Left,
    KeyCode.RIGHT -> Direction.Right,
  )
}
