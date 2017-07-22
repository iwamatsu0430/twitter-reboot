package jp.iwmat.sawtter.models.types

import jp.iwmat.sawtter.models.Iso

case class ID[A](value: Long) extends AnyVal

object ID {
  implicit def iso[A]: Iso[Long, ID[A]] = new Iso[Long, ID[A]] {
    def to(a: Long): ID[A] = ID(a)
    def from(b: ID[A]): Long = b.value
  }
}
