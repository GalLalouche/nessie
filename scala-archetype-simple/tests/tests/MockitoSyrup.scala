package tests

import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito

trait MockitoSyrup extends MockitoSugar {
	def when[T](a: T) = Mockito.when(a)

	def verify[T](a: T) = Mockito.verify(a)

	def any[T]() = org.mockito.Matchers.any[T]()
}