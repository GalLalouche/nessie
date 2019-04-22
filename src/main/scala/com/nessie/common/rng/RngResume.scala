/*
Copyright (c) 2013 National ICT Australia Limited
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
// Adapted from https://github.com/NICTA/rng
package com.nessie.common.rng

import scala.language.{higherKinds, reflectiveCalls}

import scalaz.Functor

private sealed trait RngResume[A] {
  def map[B](f: A => B): RngResume[B] = this match {
    case RngCont(x) => RngCont(x map (_ map f))
    case RngTerm(x) => RngTerm(f(x))
  }

  def term: Option[A] = this match {
    case RngCont(_) => None
    case RngTerm(x) => Some(x)
  }

  def cont: Option[Generator[Rngable[A]]] = this match {
    case RngCont(x) => Some(x)
    case RngTerm(_) => None
  }
}
private case class RngCont[A](x: Generator[Rngable[A]]) extends RngResume[A]
private case class RngTerm[A](x: A) extends RngResume[A]

private object RngResume {
  implicit val RngResumeFunctor: Functor[RngResume] = new Functor[RngResume] {
    def map[A, B](fa: RngResume[A])(f: A => B) = fa map f
  }
}

