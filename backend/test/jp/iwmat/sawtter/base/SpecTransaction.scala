package jp.iwmat.sawtter.base

import scalaz.\/

import jp.iwmat.sawtter.models.Errors
import jp.iwmat.sawtter.repositories.Transaction

case class SpecTransaction[A](value: Errors \/ A) extends Transaction[A]

object SpecTransaction {
  def right[A](value: A): SpecTransaction[A] = SpecTransaction(\/.right(value))
  def left[A](e: Errors): SpecTransaction[A] = SpecTransaction(\/.left(e))
}
