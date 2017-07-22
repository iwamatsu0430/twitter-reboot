package jp.iwmat.sawtter.models.types

import jp.iwmat.sawtter.models.Iso

case class Email[A](value: String) extends AnyVal {
  def isValid = Email.isValid(this)
}

object Email {

  implicit def iso[A]: Iso[String, Email[A]] = new Iso[String, Email[A]] {
    def to(a: String): Email[A] = Email(a)
    def from(b: Email[A]): String = b.value
  }

  val pattern = """^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""

  /**
    * Validate email.
    * Use regex for html5. Refer to https://www.w3.org/TR/html5/forms.html#valid-e-mail-address
    */
  def isValid(email: Email[_]): Boolean = {
    email.value.matches(pattern)
  }
}
