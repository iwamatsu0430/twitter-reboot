package jp.iwmat.sawtter.repositories

import scala.concurrent.Future

import scalaz.{ EitherT, Monad }

import jp.iwmat.sawtter.Result
import jp.iwmat.sawtter.models.Errors

trait RDB {
  def monad: Monad[DBResult]
  def unit: DBResult[Unit]
  def left[A](e: Errors): DBResult[A]
  def exec[A](result: DBResult[A]): Result[A]
}
