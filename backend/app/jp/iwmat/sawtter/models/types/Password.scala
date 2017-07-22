package jp.iwmat.sawtter.models.types

import jp.iwmat.sawtter.models.Iso

case class Password[A](value: String) extends AnyVal {
  def isValid = Password.isValid(this)
}

object Password {

  implicit def iso[A]: Iso[String, Password[A]] = new Iso[String, Password[A]] {
    def to(a: String): Password[A] = Password(a)
    def from(b: Password[A]): String = b.value
  }

  val pattern = """^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\d)[a-zA-Z\d]{8,32}$"""

  /**
    * Validate password.
    * - password must be 8 ~ 32 words.
    * - password must contain uppercase or lowercase alphanumeric characters each.
    */
  def isValid(password: Password[_]): Boolean = {
    password.value.matches(pattern)
  }
}
