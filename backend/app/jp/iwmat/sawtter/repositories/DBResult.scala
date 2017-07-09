package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models.Errors

trait DBResult[+A] {

  def map[B](f: A => B)(implicit rdb: RDB): DBResult[B] =
    rdb.monad.map(this)(f)

  def flatMap[B](f: A => DBResult[B])(implicit rdb: RDB): DBResult[B] =
    rdb.monad.bind(this)(f)
}

object DBResult {

  def unit(implicit rdb: RDB): DBResult[Unit] =
    rdb.unit

  def getOrElse[A](opt: Option[A])(e: Errors)(implicit rdb: RDB): DBResult[A] =
    opt.map(rdb.monad.point(_)).getOrElse(left(e))

  case class DBResultEither(condition: Boolean) {
    def or(e: Errors)(implicit rdb: RDB): DBResult[_] =
      if (condition) DBResult.unit else DBResult.left(e)
  }

  def either(condition: => Boolean): DBResultEither =
    DBResultEither(condition)

  def left[A](e: Errors)(implicit rdb: RDB): DBResult[A] =
    rdb.left[A](e)
}
