package com.nessie.model.map

import scala.collection.JavaConverters._

import common.rich.primitives.RichBoolean._

object DictGrid extends GridFactory {
  override def apply[A](gs: GridSize, default: A): Grid[A] =
    Impl(Map(), width = gs.width, height = gs.height, default = Some(default))
  override def apply[A](rows: Seq[Seq[A]]): Grid[A] = {
    val vr = GridFactoryHelper.verify(rows)
    val gs = vr.gs
    val map = (for {
      (v, y) <- vr.rows.zipWithIndex
      (a, x) <- v.zipWithIndex
    } yield MapPoint(x, y) -> a).toMap
    Impl(objects = map, width = gs.width, height = gs.height, default = None)
  }

  private case class Impl[A](
      private val objects: Map[MapPoint, A],
      override val width: Int,
      override val height: Int,
      private val default: Option[A],
  ) extends Grid[A] {
    override def apply(p: MapPoint) = objects.get(p).orElse(default).get
    override def place(p: MapPoint, o: A): Impl[A] =
      copy(objects = if (default.contains(o)) objects - p else objects + (p -> o))
    override def map[B](f: A => B): Grid[B] = copy(objects = objects.mapValues(f), default = default.map(f))
    override def mapPoints[B](f: (MapPoint, A) => B): Grid[B] = {
      val $ = new java.util.HashMap[MapPoint, B]()
      val newDefault = default.map(f(MapPoint(0, 0), _))
      for (
        x <- 0 until width;
        y <- 0 until height;
        mp = MapPoint(x, y);
        value = f(mp, apply(mp))
        if newDefault.contains(value).isFalse
      ) $.put(mp, value)
      copy(objects = $.asScala.toMap, default = newDefault)
    }
  }
}
