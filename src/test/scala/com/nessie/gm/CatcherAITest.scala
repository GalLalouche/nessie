package com.nessie.gm

import com.nessie.gm.TurnAction.EndTurn
import com.nessie.model.eq.EventQueue
import com.nessie.model.map.{CombatUnitObject, MapPoint, VectorBattleMap}
import com.nessie.model.units.{Skeleton, Warrior}
import common.AuxSpecs
import org.mockito.Mockito
import org.scalatest.{FreeSpec, OneInstancePerTest}
import org.scalatest.mockito.MockitoSugar

class CatcherAITest extends FreeSpec with AuxSpecs with MockitoSugar with OneInstancePerTest {
  private val unit = Skeleton.create
  private val map = VectorBattleMap(2, 2)
      .place(MapPoint(0, 0), CombatUnitObject(unit))
      .place(MapPoint(0, 1), CombatUnitObject(Warrior.create))
  private val converter = mock[AbilityToTurnActionConverter]
  "attacks when adjacent to player and pre-action" in {
    val gs = GameState(
      map,
      new EventQueue(),
      Some(PreAction.empty(unit))
    )
    val expected = mock[TurnAction]
    Mockito.when(converter.apply(unit.attackAbility)(MapPoint(0, 0), MapPoint(0, 1))).thenReturn(expected)
    new CatcherAI(converter).apply(unit)(gs) shouldReturn expected
  }
  "doesn't move if adjacent to a player post action" in {
    val gs = GameState(
      map,
      new EventQueue(),
      Some(mock[PostAction])
    )
    new CatcherAI(converter).apply(unit)(gs) shouldReturn EndTurn
  }
}
