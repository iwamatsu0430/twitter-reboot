package jp.iwmat.sawtter.models

import jp.iwmat.sawtter.models.types.{ Email, Password }

case class SignUp(
  email: Email[SignUp],
  password: Password[SignUp]
)
