package com.nessie.view

import org.hexworks.zircon.api.component.ComponentBuilder

package object zirconview {
  private[zirconview] type Placer = ComponentBuilder[_, _] => Any
}
