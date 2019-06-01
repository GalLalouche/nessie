package com.nessie.model.map

case class DictGrid[A] private(
    objects: Map[MapPoint, A],
    override val width: Int,
    override val height: Int,
    default: A
) extends Grid[A] {
  override def apply(p: MapPoint) = objects.getOrElse(p, default)
  override def place(p: MapPoint, o: A): DictGrid[A] =
    copy(objects = if (o == EmptyMapObject) objects - p else objects + (p -> o))
  override def map(f: A => A): Grid[A] = copy(objects.mapValues(f))
}

object DictGrid extends GridFactory {
  override def apply[A](gs: GridSize, default: A) =
    new DictGrid(Map(), width = gs.width, height = gs.height, default = default)
}
