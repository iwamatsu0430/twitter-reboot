package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

case class Comment(
  commentId: Long,
  pageId: Long,
  userId: Long,
  comment: String,
  createdAt: ZonedDateTime
)
