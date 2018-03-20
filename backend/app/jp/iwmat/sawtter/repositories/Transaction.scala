package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.Result
import jp.iwmat.sawtter.models.Errors

trait Transaction[A] {

  def map[B](f: A => B)(implicit rdb: RDB): Transaction[B] =
    rdb.monad.map(this)(f)

  def flatMap[B](f: A => Transaction[B])(implicit rdb: RDB): Transaction[B] =
    rdb.monad.bind(this)(f)

  def execute()(implicit rdb: RDB): Result[A] =
    rdb.exec(this)
}

object Transaction {

  def apply[A](a: A): Transaction[A] = new Transaction[A] {}

  def unit(implicit rdb: RDB): Transaction[Unit] =
    rdb.unit

  def getOrElse[A](opt: Option[A])(e: Errors)(implicit rdb: RDB): Transaction[A] =
    opt.map(rdb.monad.point(_)).getOrElse(left(e))

  case class TransactionEither(condition: Boolean) {
    def or(e: Errors)(implicit rdb: RDB): Transaction[_] =
      if (condition) Transaction.unit else Transaction.left(e)
  }

  def either(condition: => Boolean): TransactionEither =
    TransactionEither(condition)

  def left[A](e: Errors)(implicit rdb: RDB): Transaction[A] =
    rdb.left[A](e)
}
