package jp.iwmat.sawtter.models.types

import jp.iwmat.sawtter.models.Iso

case class Token[A](value: String)

object Token {

  implicit def iso[A]: Iso[String, Token[A]] = new Iso[String, Token[A]] {
    def to(a: String): Token[A] = Token(a)
    def from(b: Token[A]): String = b.value
  }
}
