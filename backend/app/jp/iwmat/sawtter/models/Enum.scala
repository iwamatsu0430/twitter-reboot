package jp.iwmat.sawtter.models

import scalaz.\/

trait Enum[A] {
  def value: A
}

trait EnumCompanion[A, B <: Enum[A]] {
  def values: Seq[B]
  def valueOf(value: A): Errors \/ B = values
    .find(_.value == value)
    .map(\/.right)
    .getOrElse(\/.left(Errors.EnumNotFound(value)))
}
