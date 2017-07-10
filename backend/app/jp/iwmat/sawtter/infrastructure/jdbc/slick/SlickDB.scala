package jp.iwmat.sawtter.infrastructure.jdbc._slick

import javax.inject.Inject

import scala.concurrent.{ ExecutionContext, Future }

import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import scalaz.{ \/, EitherT, Monad }
import slick.dbio.DBIO
import slick.driver.JdbcProfile

import jp.iwmat.sawtter.Result
import jp.iwmat.sawtter.models.Errors
import jp.iwmat.sawtter.repositories._

trait DBIOResult[A] extends DBResult[A] {
  val value: EitherT[DBIO, Errors, A]
}

object DBIOResult {

  def apply[A](dbio: DBIO[A])(implicit ec: ExecutionContext): DBIOResult[A] = {
    val dis: DBIO[Errors \/ A] = dbio.map(\/.right[Errors, A])
    new DBIOResult[A] {
      val value = EitherT(dis)
    }
  }
}

class SlickDB @Inject()(
  val dbConfigProvider: DatabaseConfigProvider
)(
  implicit
  ec: ExecutionContext
) extends RDB with HasDatabaseConfigProvider[JdbcProfile] {

  def exitByUnexpectedType() = throw new Exception("xxx") // FIXME

  implicit val dbioMonad: Monad[DBIO] = new Monad[DBIO] {
    def point[A](value: => A): DBIO[A] = DBIO.successful(value)
    def bind[A, B](fa: DBIO[A])(f: A => DBIO[B]): DBIO[B] = fa.flatMap(f)
  }

  val monad: Monad[DBResult] = new Monad[DBResult] {

    def point[A](value: => A): DBResult[A] = DBIOResult(DBIO.successful(value))

    def bind[A, B](fa: DBResult[A])(f: A => DBResult[B]): DBResult[B] = fa match {
      case dbio: DBIOResult[A] => {
        new DBIOResult[B] {
          val value = dbio.value.flatMap { v =>
            f(v) match {
              case d: DBIOResult[B] => d.value
              case _ => exitByUnexpectedType()
            }
          }
        }
      }
      case _ => exitByUnexpectedType()
    }
  }

  val unit: DBResult[Unit] = {
    val dbio: DBIO[Errors \/ Unit] = DBIO.successful(\/.right[Errors, Unit](()))
    new DBIOResult[Unit] {
      val value = EitherT(dbio)
    }
  }

  def left[A](e: Errors): DBResult[A] = {
    val dbio: DBIO[Errors \/ A] = DBIO.successful(\/.left[Errors, A](e))
    new DBIOResult[A] {
      val value = EitherT(dbio)
    }
  }

  def exec[A](result: DBResult[A]): Result[A] = result match {
    case dbio: DBIOResult[A] => EitherT(db.run(dbio.value.run))
    case _ => exitByUnexpectedType()
  }
}
