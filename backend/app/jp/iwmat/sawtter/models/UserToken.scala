package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

case class UserToken(
  userId: Long,
  token: String,
  expiredAt: ZonedDateTime
)
