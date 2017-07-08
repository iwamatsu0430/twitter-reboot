package jp.iwmat.sawtter.syntax

import scalaz.{ \/, EitherT }

import jp.iwmat.sawtter.models.Errors

trait ToEitherOps {

  implicit class DisjunctionToEitherOps[F[_], A](fa: F[Errors \/ A]) {
    def et: EitherT[F, Errors, A] = {
      EitherT(fa)
    }
  }
}
