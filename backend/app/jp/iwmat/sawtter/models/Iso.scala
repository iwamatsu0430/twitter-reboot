package jp.iwmat.sawtter.models

trait Iso[A, B] {
  def to(a: A): B
  def from(b: B): A
}
