package com.nessie.gm

import com.nessie.gm.GameStateChange.{ActionTaken, NoOp}
import com.nessie.gm.TurnAction.{ActualAction, EndTurn, MovementAction, StartTurn}
import com.nessie.model.map.MapPoint
import com.nessie.model.units.CombatUnit
import common.AuxSpecs
import org.mockito.Mockito
import org.scalatest.FreeSpec
import org.scalatest.mockito.MockitoSugar

class ChangeLoggerTest extends FreeSpec with AuxSpecs with MockitoSugar {
  "toString" - {
    "NoOp returns None" in {
      ChangeLogger toString NoOp shouldReturn None
    }
    "StartTurn" in {
      val unit = mock[CombatUnit]
      Mockito.when(unit.toString).thenReturn("mockUnit")
      ChangeLogger.toString(ActionTaken(StartTurn(unit))).get should not be 'empty
    }
    "EndTurn" in {
      ChangeLogger.toString(ActionTaken(EndTurn)).get should not be 'empty
    }
    "MovementAction" in {
      ChangeLogger.toString(
        ActionTaken(MovementAction(Movement(MapPoint(0, 1), MapPoint(1, 0))))
      ).get should not be 'empty
    }
    "ActualAction" - {
      "Attack" in {
        ChangeLogger.toString(
          ActionTaken(ActualAction(Attack(MapPoint(0, 1), MapPoint(1, 0), 5, 0.25)))
        ).get should not be 'empty
      }
    }
  }
}
