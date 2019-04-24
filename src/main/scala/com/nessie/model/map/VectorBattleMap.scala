package com.nessie.model.map

import common.rich.RichT._

/**
 * Trades space for (amortized) performance. Unlike DictMap which doesn't need to save empty objects, this
 * class does. In addition, it has to allocate the entire map on construction. However, since it doesn't need
 * to do any hashing of map points it has much better indexing performance.
 */
case class VectorBattleMap private(
    grid: Vector[Vector[BattleMapObject]],
    verticalBetweens: Vector[Vector[BetweenMapObject]],
    horizontalBetweens: Vector[Vector[BetweenMapObject]],
    override val width: Int,
    override val height: Int,
) extends BattleMap(width, height) {
  override def apply(p: MapPoint) = grid(p.x)(p.y)
  private def canonicalCoordinates(pd: DirectionalMapPoint): MapPoint = MapPoint(
    x = pd.x.mapIf(pd.direction == Direction.Right).to(_ + 1),
    y = pd.y.mapIf(pd.direction == Direction.Down).to(_ + 1),
  )
  override def apply(pd: DirectionalMapPoint) = {
    val p = canonicalCoordinates(pd)
    if (pd.direction == Direction.Up || pd.direction == Direction.Down) verticalBetweens(p.x)(p.y)
    else horizontalBetweens(p.x)(p.y)
  }

  // TODO lenses?
  override def internalPlace(p: MapPoint, o: BattleMapObject) =
    copy(grid = grid.updated(p.x, grid(p.x).updated(p.y, o)))
  override def internalPlace(pd: DirectionalMapPoint, o: BetweenMapObject) = {
    val p = canonicalCoordinates(pd)
    if (pd.direction == Direction.Up || pd.direction == Direction.Down)
      copy(verticalBetweens = verticalBetweens.updated(p.x, verticalBetweens(p.x).updated(p.y, o)))
    else
      copy(horizontalBetweens = horizontalBetweens.updated(p.x, horizontalBetweens(p.x).updated(p.y, o)))
  }
}

object VectorBattleMap {
  def apply(width: Int, height: Int): VectorBattleMap = new VectorBattleMap(
    grid = Vector.fill(width, height)(EmptyMapObject),
    verticalBetweens = Vector.fill(width, height + 1)(EmptyBetweenMapObject),
    horizontalBetweens = Vector.fill(width + 1, height)(EmptyBetweenMapObject),
    width = width,
    height = height,
  )
}
