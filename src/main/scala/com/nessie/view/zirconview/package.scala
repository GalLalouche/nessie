package com.nessie.view

import org.hexworks.zircon.api.uievent.{KeyboardEvent, KeyCode, MouseEvent}
import rx.lang.scala.Observable

package object zirconview {
  type KeyboardEvents = Observable[KeyboardEvent]
  type KeyCodes = Observable[KeyCode]
  type SimpleKeyboardEvents = Observable[Char]
  type MouseEvents = Observable[MouseEvent]
}
