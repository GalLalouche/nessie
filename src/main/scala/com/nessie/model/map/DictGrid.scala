package com.nessie.model.map

case class DictGrid[A] private(
    private val objects: Map[MapPoint, A],
    override val width: Int,
    override val height: Int,
    private val default: Option[A],
) extends Grid[A] {
  override def apply(p: MapPoint) = objects.get(p).orElse(default).get
  override def place(p: MapPoint, o: A): DictGrid[A] =
    copy(objects = if (default.contains(o)) objects - p else objects + (p -> o))
  override def map[B](f: A => B): Grid[B] = copy(objects = objects.mapValues(f), default = default.map(f))
}

object DictGrid extends GridFactory {
  override def apply[A](gs: GridSize, default: A) =
    new DictGrid(Map(), width = gs.width, height = gs.height, default = Some(default))
  override def apply[A](rows: Seq[Seq[A]]): Grid[A] = {
    val vr = GridFactoryHelper.verify(rows)
    val gs = vr.gs
    val map = (for {
      (v, y) <- vr.rows.zipWithIndex
      (a, x) <- v.zipWithIndex
    } yield MapPoint(x, y) -> a).toMap
    DictGrid(objects = map, width = gs.width, height = gs.height, default = None)
  }
}
