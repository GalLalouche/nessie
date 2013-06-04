package tests

import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito

trait MockitoSyrup extends MockitoSugar {
	def when[T](a: T) = Mockito.when(a)
}