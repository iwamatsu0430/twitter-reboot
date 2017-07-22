package jp.iwmat.sawtter.models.types

import jp.iwmat.sawtter.models.Iso

case class URL[A](value: String) extends AnyVal {
  def isValid = URL.isValid(this)
}

object URL {

  implicit def iso[A]: Iso[String, URL[A]] = new Iso[String, URL[A]] {
    def to(a: String): URL[A] = URL(a)
    def from(b: URL[A]): String = b.value
  }

  /**
    * Validate url.
    * - starts with 'http://' or 'https://'
    */
  def isValid(url: URL[_]): Boolean = {
    url.value.startsWith("http://") || url.value.startsWith("https://")
  }
}
