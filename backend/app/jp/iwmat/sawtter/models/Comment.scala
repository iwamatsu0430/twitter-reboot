package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

case class Comment(
  commentId: Long,
  url: String,
  userId: Long,
  text: String,
  createdAt: ZonedDateTime
)
