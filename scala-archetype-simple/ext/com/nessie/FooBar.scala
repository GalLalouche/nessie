package com.nessie

import com.nessie.map.ctrl.SwingBattleMapController
import scala.swing.Reactor
import scala.swing.Publisher
import com.nessie.map.model.BattleMap
import com.nessie.map.model.ArrayBattleMap
import com.nessie.map.view.StringBattleMapViewer
import com.nessie.map.view.swing.MapPanel
import com.nessie.map.view.swing.SimpleSwingBuilder
import com.nessie.map.objects.CombatUnitObject
import com.nessie.units.Skeleton
import akka.actor.ActorSystem
import akka.actor.Props

object FooBar extends App {
	val m = ArrayBattleMap(5, 5)
		.place((0, 0), new CombatUnitObject(new Skeleton))
	val system = ActorSystem("foobar")
	system.actorOf(Props(new SwingBattleMapController(m))) ! SwingBattleMapController.Startup
}