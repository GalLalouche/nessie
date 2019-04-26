package com.nessie.model.map

/**
 * Trades space for (amortized) performance. Unlike DictMap which doesn't need to save empty objects, this
 * class does. In addition, it has to allocate the entire map on construction. However, since it doesn't need
 * to do any hashing of map points it has much better indexing performance.
 */
case class VectorSimpleBattleMap private(
    grid: Vector[Vector[BattleMapObject]],
    override val width: Int,
    override val height: Int,
) extends SimpleBattleMap(width, height) {
  override def apply(p: MapPoint) = grid(p.x)(p.y)
  // TODO lenses?
  @inline override final def internalPlace(p: MapPoint, o: BattleMapObject) =
    copy(grid = grid.updated(p.x, grid(p.x).updated(p.y, o)))
}

object VectorSimpleBattleMap {
  def apply(width: Int, height: Int): VectorSimpleBattleMap = new VectorSimpleBattleMap(
    grid = Vector.fill(width, height)(EmptyMapObject),
    width = width,
    height = height,
  )
}
