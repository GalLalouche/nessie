package com.nessie.model.map

import org.scalatest.FreeSpec

import common.test.AuxSpecs

class GridParserTest extends FreeSpec with AuxSpecs {
  "Simple" in {
    GridParser.fromFactory(VectorGrid).parse("123\n456") shouldReturn
        VectorGrid(GridSize(width = 3, height = 2), 0)
            .place(MapPoint(0, 0), 1)
            .place(MapPoint(1, 0), 2)
            .place(MapPoint(2, 0), 3)
            .place(MapPoint(0, 1), 4)
            .place(MapPoint(1, 1), 5)
            .place(MapPoint(2, 1), 6)
  }
}
