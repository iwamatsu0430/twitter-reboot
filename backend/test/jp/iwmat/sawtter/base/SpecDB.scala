package jp.iwmat.sawtter.base

import scala.concurrent.Future

import scalaz.{ \/, -\/, \/-, EitherT, Monad }

import jp.iwmat.sawtter.Result
import jp.iwmat.sawtter.models.Errors
import jp.iwmat.sawtter.repositories._

case class SpecDB(execHook: Errors \/ _ => Unit) extends RDB {

  def exitByUnexpectedType() = throw new Exception("Transaction is not SpecTransaction. Confirm Mock repositories.")

  def monad: Monad[Transaction] = new Monad[Transaction] {
    def point[A](value: => A): Transaction[A] = SpecTransaction(\/-(value))

    def bind[A, B](fa: Transaction[A])(f: A => Transaction[B]): Transaction[B] = fa match {
      case spec: SpecTransaction[A] => {
        spec.value match {
          case \/-(v) => f(v) match {
            case result: SpecTransaction[B] => result
            case _ => exitByUnexpectedType()
          }
          case -\/(e) => SpecTransaction(-\/(e))
        }
      }
      case _ => exitByUnexpectedType()
    }
  }

  def unit: Transaction[Unit] = SpecTransaction(\/-(()))

  def left[A](e: Errors): Transaction[A] = SpecTransaction(-\/(e))

  def exec[A](result: Transaction[A]): Result[A] = result match {
    case spec: SpecTransaction[A] => {
      execHook.apply(spec.value)
      EitherT(Future.successful(spec.value))
    }
    case _ => exitByUnexpectedType()
  }
}
