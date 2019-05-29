package com.nessie.view.zirconview.map

import com.nessie.model.map.MapPoint
import org.hexworks.zircon.api.data.Position

/**
 * Converts between [[MapPoint]]s and [[Position]]s. There are two reasons why they aren't isomorphic:
 * 1. The map view itself is not necessarily in the top left corner of the screen, i.e., MapPoint(0, 0) isn't
 *    in absolute Position(0, 0).
 * 2. The map view itself can be scrolled if the map is larger than the map view, i.e., *relative*
 *    Position(0, 0) isn't necessarily MapPoint(0, 0).
 *
 * Absolute and relative positions:
 * Absolute position is the position of a tile within the entire visible screen. Since there are other views
 * than the map, and since the map isn't necessarily pinned to top left corner, not every absolute position is
 * part of the map view (or indeed, part of the screen at all). Absolute positions can be used to create
 * Modals at the position.
 *
 * Relative position is the position relative to the top level corner of the map view. If the map is not
 * scrolled, this would be MapPoint(0, 0), but that's not necessarily so. Also, since the map view isn't
 * infinite, it's possible for a relative position to be outside of the bounds of the map. However, since
 * This Should Never Happen, an exception is thrown in such cases instead of returning an Option. Relative
 * positions are used by updating layer graphics.
 *
 * It's always true that relativePosition(mp).withRelative(mapViewPosition) == absolutePosition(mp).
 *
 * Note that a MapPoint is always absolute, and refers to a concrete location in the map Grid. Whether said
 * MapPoint is currently visible depends on the size of the current [[com.nessie.model.map.BattleMap]] and the
 * active map view. A MapPoint can also be invalid if it is outside the bounds of the current
 * [[com.nessie.model.map.BattleMap]] grid.
 */
private[zirconview] trait MapPointConverter {
  def toAbsolutePosition(mp: MapPoint): Option[Position]
  def toRelativePosition(mp: MapPoint): Option[Position]

  def fromAbsolutePosition(p: Position): Option[MapPoint]
  def fromRelativePosition(p: Position): MapPoint

  def isInBounds(mp: MapPoint): Boolean
  def isAbsolutePositionInBounds(p: Position): Boolean
  def isRelativePositionInBounds(p: Position): Boolean
}
