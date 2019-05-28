package com.nessie.gm

import com.nessie.model.map.GridSize

trait DebugMapStepperFactory {
  def apply(gridSize: GridSize): DebugMapStepper
}
