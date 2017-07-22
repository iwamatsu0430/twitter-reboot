package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

import jp.iwmat.sawtter.models.types.{ Email, ID, Version }

case class User(
  userId: ID[User],
  email: Email[User],
  status: UserStatus,
  version: Version[User],
  updatedAt: ZonedDateTime,
  createdAt: ZonedDateTime
)

object User {

  def isValidForSignUp(userOpt: Option[User]): Boolean =
    userOpt.isEmpty || userOpt.exists(_.status == UserStatus.Disabled)
}
