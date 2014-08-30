package com.nessie.tests

import org.mockito.Mockito
import org.mockito.verification.VerificationMode
import org.scalatest.mock.MockitoSugar

trait MockitoSyrup extends MockitoSugar {
	def when[T](a: T) = Mockito.when(a)

	def verify[T](a: T) = Mockito.verify(a)

	def verify[T](a: T, v: VerificationMode) = Mockito.verify(a, v)

	def any[T]() = org.mockito.Matchers.any[T]()
}