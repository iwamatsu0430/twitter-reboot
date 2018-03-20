package jp.iwmat.sawtter.repositories

import scala.concurrent.Future

import scalaz.{ EitherT, Monad }

import jp.iwmat.sawtter.Result
import jp.iwmat.sawtter.models.Errors

trait RDB {
  def monad: Monad[Transaction]
  def unit: Transaction[Unit]
  def left[A](e: Errors): Transaction[A]
  def exec[A](result: Transaction[A]): Result[A]
}
