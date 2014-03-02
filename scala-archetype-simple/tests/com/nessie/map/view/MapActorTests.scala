package com.nessie.map.view

import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.OneInstancePerTest
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.ImplicitSender
import akka.testkit.TestKitBase
import tests.MockitoSyrup
import akka.actor.ActorSystem

class MapActorTests extends FlatSpec with ShouldMatchers with MockitoSyrup with OneInstancePerTest with BeforeAndAfter
	with TestKitBase with ImplicitSender {
	implicit lazy val system = ActorSystem()
}
