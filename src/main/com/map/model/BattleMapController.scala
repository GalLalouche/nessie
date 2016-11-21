package com.nessie.map.model

import com.nessie.map.exceptions.{MapEmptyException, MapOccupiedException}
import com.nessie.model.map.objects.{BattleMapObject, EmptyMapObject}
import com.nessie.view.map.{MapView, MapViewFactory}

/** A mutable wrapper of BattleMap */
class BattleMapController(private[model] var currentMap: BattleMap, viewFactory: MapViewFactory) {
	private var currentView: MapView = null
	private def verify(b: Boolean, e: Exception): BattleMapController = if (b) throw e else this
	private def shouldBeEmpty(p: MapPoint) = verify(currentMap isOccupiedAt p, new MapOccupiedException(p))
	private def shouldBeOccupied(p: MapPoint) = verify(false == (currentMap isOccupiedAt p), new MapEmptyException(p))

	private def update(p: MapPoint, o: BattleMapObject): BattleMapController = {
		currentMap = currentMap.updated(p, o)
		currentView.stop()
		currentView = viewFactory(currentMap, this)
		currentView.start()
		this
	}

	def place(p: MapPoint, o: BattleMapObject): BattleMapController = shouldBeEmpty(p).update(p, o)
	def remove(p: MapPoint): BattleMapController = shouldBeOccupied(p).update(p, EmptyMapObject)
	def move(from: MapPoint) = new {
		val source = currentMap(from)
		def to(to: MapPoint): BattleMapController = remove(from).place(to, source)
	}

	def start() {
		if (currentView != null)
			throw new IllegalStateException("Already started")
		currentView = viewFactory(currentMap, this)
		currentView.start()
	}
}

object BattleMapController {
	def apply(map: BattleMap, viewFactory: MapViewFactory) = new BattleMapController(map, viewFactory)
}
