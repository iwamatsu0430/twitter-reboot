package jp.iwmat

import scala.concurrent.{ ExecutionContext, Future }

import scalaz.{ \/, \/-, EitherT }

import jp.iwmat.sawtter.models.Errors

package object sawtter {
  type Result[A] = EitherT[Future, Errors, A]

  object Result {
    def apply[A](value: A): Result[A] = {
      val either: Errors \/ A = \/-(value)
      EitherT(Future.successful(either))
    }

    def apply[A](value: Errors \/ A): Result[A] = {
      EitherT(Future.successful(value))
    }

    def apply[A](future: Future[A])(implicit ec: ExecutionContext): Result[A] = {
      val eitherF: Future[Errors \/ A] = future.map(a => \/-(a))
      EitherT(eitherF)
    }
  }
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
