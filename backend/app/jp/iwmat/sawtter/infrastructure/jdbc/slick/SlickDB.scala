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

trait DBIOTransaction[A] extends Transaction[A] {
  val value: EitherT[DBIO, Errors, A]
}

object DBIOTransaction {

  def apply[A](dbio: DBIO[A])(implicit ec: ExecutionContext): DBIOTransaction[A] = {
    val dis: DBIO[Errors \/ A] = dbio.map(\/.right[Errors, A])
    new DBIOTransaction[A] {
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

  def exitByUnexpectedType() = throw new Exception("Transaction is not DBIOTransaction. Confirm DI settings.")

  implicit val dbioMonad: Monad[DBIO] = new Monad[DBIO] {
    def point[A](value: => A): DBIO[A] = DBIO.successful(value)
    def bind[A, B](fa: DBIO[A])(f: A => DBIO[B]): DBIO[B] = fa.flatMap(f)
  }

  val monad: Monad[Transaction] = new Monad[Transaction] {

    def point[A](value: => A): Transaction[A] = DBIOTransaction(DBIO.successful(value))

    def bind[A, B](fa: Transaction[A])(f: A => Transaction[B]): Transaction[B] = fa match {
      case dbio: DBIOTransaction[A] => {
        new DBIOTransaction[B] {
          val value = dbio.value.flatMap { v =>
            f(v) match {
              case d: DBIOTransaction[B] => d.value
              case _ => exitByUnexpectedType()
            }
          }
        }
      }
      case _ => exitByUnexpectedType()
    }
  }

  val unit: Transaction[Unit] = {
    val dbio: DBIO[Errors \/ Unit] = DBIO.successful(\/.right[Errors, Unit](()))
    new DBIOTransaction[Unit] {
      val value = EitherT(dbio)
    }
  }

  def left[A](e: Errors): Transaction[A] = {
    val dbio: DBIO[Errors \/ A] = DBIO.successful(\/.left[Errors, A](e))
    new DBIOTransaction[A] {
      val value = EitherT(dbio)
    }
  }

  def exec[A](result: Transaction[A]): Result[A] = result match {
    case dbio: DBIOTransaction[A] => EitherT(db.run(dbio.value.run))
    case _ => exitByUnexpectedType()
  }
}
