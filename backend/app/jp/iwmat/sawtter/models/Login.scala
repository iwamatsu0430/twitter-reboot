package jp.iwmat.sawtter.models

import jp.iwmat.sawtter.models.types.{ Email, Password }

case class Login(
  email: Email[Login],
  password: Password[Login]
)
