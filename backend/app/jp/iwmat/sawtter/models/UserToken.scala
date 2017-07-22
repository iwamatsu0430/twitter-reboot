package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

import jp.iwmat.sawtter.models.types.{ ID, Token }

case class UserToken(
  userId: ID[User],
  token: Token[UserToken],
  expiredAt: ZonedDateTime
)
