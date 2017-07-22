package jp.iwmat.sawtter.models.types

import jp.iwmat.sawtter.models.Iso

case class Version[A](value: Long) extends AnyVal {
  def next: Version[A] = copy(value + 1)
}

object Version {

  def init[A]: Version[A] = Version(1L)

  implicit def iso[A]: Iso[Long, Version[A]] = new Iso[Long, Version[A]] {
    def to(a: Long): Version[A] = Version(a)
    def from(b: Version[A]): Long = b.value
  }
}
