package com.nessie.view.zirconview

import org.hexworks.zircon.api.component.modal.ModalResult

private case class ModalResultWrapper[A](value: A) extends ModalResult
