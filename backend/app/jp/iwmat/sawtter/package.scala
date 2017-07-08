package jp.iwmat

import scala.concurrent.{ ExecutionContext, Future }

import scalaz.EitherT

import jp.iwmat.sawtter.models.Errors

package object sawtter extends ToEitherOps {
  type Result[A] = scalaz.EitherT[Future, Errors, A]
}

trait ToEitherOps {
  import slick.dbio.DBIO
  implicit class EitherOps[A](a: DBIO[A]) {
    import scalaz._
    def et(implicit ec: ExecutionContext): EitherT[DBIO, Errors, A] = {
      val dbio: DBIO[Errors \/ A] = a.map(\/.right[Errors, A])
      EitherT(dbio)
    }
  }
}
