package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

case class User(
  userId: Long,
  email: String,
  status: UserStatus,
  version: Long,
  updatedAt: ZonedDateTime,
  createdAt: ZonedDateTime
)

object User {

  def isValidForSignUpUser(userOpt: Option[User]): Boolean =
    userOpt.isEmpty || userOpt.exists(_.status == UserStatus.Disabled)
}
