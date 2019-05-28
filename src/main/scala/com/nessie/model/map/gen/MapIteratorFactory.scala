package com.nessie.model.map.gen

import com.nessie.model.map.GridSize

trait MapIteratorFactory {
  def generate(gs: GridSize): MapIterator
}

