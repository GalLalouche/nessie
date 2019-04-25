package com.nessie.model.map

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
  // Since the apply methods are called very often, we opt for performance over nicer code here.
  @inline private def canonicalX(pd: DirectionalMapPoint): Int =
    if (pd.direction eq Direction.Right) pd.x + 1 else pd.x
  @inline private def canonicalY(pd: DirectionalMapPoint): Int =
    if (pd.direction eq Direction.Down) pd.y + 1 else pd.y
  @inline private def isVertical(pd: DirectionalMapPoint) =
    pd.direction.eq(Direction.Up) || pd.direction.eq(Direction.Down)
  @inline override final def apply(pd: DirectionalMapPoint) = {
    val x = canonicalX(pd)
    val y = canonicalY(pd)
    if (isVertical(pd)) verticalBetweens(x)(y) else horizontalBetweens(x)(y)
  }

  // TODO lenses?
  @inline override final def internalPlace(p: MapPoint, o: BattleMapObject) =
    copy(grid = grid.updated(p.x, grid(p.x).updated(p.y, o)))
  @inline override final def internalPlace(pd: DirectionalMapPoint, o: BetweenMapObject) = {
    val x = canonicalX(pd)
    val y = canonicalY(pd)
    if (isVertical(pd))
      copy(verticalBetweens = verticalBetweens.updated(x, verticalBetweens(x).updated(y, o)))
    else
      copy(horizontalBetweens = horizontalBetweens.updated(x, horizontalBetweens(x).updated(y, o)))
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
