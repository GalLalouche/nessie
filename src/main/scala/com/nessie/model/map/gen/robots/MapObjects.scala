package com.nessie.model.map.gen.robots

import com.nessie.model.map.BattleMapObject

private sealed trait MapObjects extends BattleMapObject

private case class Tunnel(generation: Int) extends MapObjects
private case class Junction(generation: Int) extends MapObjects
private case class Room(generation: Int) extends MapObjects
private case class Door(generation: Int) extends MapObjects
