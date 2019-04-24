package com.nessie.model.map.gen

import com.nessie.model.map.BattleMapObject

private sealed trait AlgorithmStepMapObject extends BattleMapObject
// All the indices below are used for debugging and previewing the algorithm steps.
private case class RoomMapObject(index: Int) extends AlgorithmStepMapObject
private case class TunnelMapObject(index: Int) extends AlgorithmStepMapObject
private case class ReachableMapObject(original: AlgorithmStepMapObject, index: Int)
    extends AlgorithmStepMapObject
