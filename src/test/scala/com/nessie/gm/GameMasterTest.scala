package com.nessie.gm

import com.nessie.gm.TurnAction.EndTurn
import com.nessie.model.map.{CombatUnitObject, FullWall, MapPoint, VectorBattleMap}
import com.nessie.model.units.{Archer, CombatUnit, Skeleton, Warrior, Zombie}
import com.nessie.model.units.Owner.Player
import common.rich.collections.RichIterator._
import common.AuxSpecs
import common.rich.RichT._
import common.rich.func.{MoreIteratorInstances, ToMoreMonadPlusOps}
import org.scalatest.time.SpanSugar._
import org.scalatest.FreeSpec
import org.scalatest.tags.Slow

import scala.language.{postfixOps, reflectiveCalls}

import scalaz.concurrent.Task

@Slow
class GameMasterTest extends FreeSpec with AuxSpecs with ToMoreMonadPlusOps with MoreIteratorInstances {
  "If the player always ends the turn doing nothing, eventually all the player units die" in {
    // Tests the AI too.
    def hasRemainingPlayers(gs: GameState): Boolean =
      gs.map.objects.iterator.map(_._2).select[CombatUnitObject].exists(_.unit.owner == Player)
    val gm = GameMaster.initiate(
      state = GameState.fromMap(
        VectorBattleMap(5, 5)
            .place(MapPoint(0, 0), CombatUnitObject(Warrior.create))
            .place(MapPoint(0, 1), CombatUnitObject(Archer.create))
            .place(MapPoint(1, 0), FullWall)
            .place(MapPoint(4, 4), CombatUnitObject(Skeleton.create))
            .place(MapPoint(3, 4), CombatUnitObject(Zombie.create))
      ),
      playerInput = new PlayerInput {
        override def nextState(currentlyPlayingUnit: CombatUnit)(gs: GameState) = Task.now(EndTurn)
      })

    {gm.takeWhile(_._2 |> hasRemainingPlayers).last()} shouldFinish in lessThan 2000.millis
  }

  "AI versus AI eventually kills all players in one side" in {
    // Tests the AI too.
    def hasMultipleOwners(gs: GameState): Boolean =
      gs.map.objects.iterator.map(_._2).select[CombatUnitObject].map(_.unit.owner).toSet.size == 2
    val gm = GameMaster.initiate(
      state = GameState.fromMap(
        VectorBattleMap(5, 5)
            .place(MapPoint(0, 0), CombatUnitObject(Warrior.create))
            .place(MapPoint(0, 1), CombatUnitObject(Archer.create))
            .place(MapPoint(1, 0), FullWall)
            .place(MapPoint(4, 4), CombatUnitObject(Skeleton.create))
            .place(MapPoint(3, 4), CombatUnitObject(Zombie.create))
      ),
      playerInput = PlayerInput.fromAI(CatcherAI)
    )

    {gm.takeWhile(_._2 |> hasMultipleOwners).last()} shouldFinish in lessThan 2000.millis
  }
}
