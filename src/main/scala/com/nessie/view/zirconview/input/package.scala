package com.nessie.view.zirconview

import org.hexworks.zircon.api.component.modal.Modal

package object input {
  private[zirconview] type WrappedModal[A] = Modal[ModalResultWrapper[A]]
}
