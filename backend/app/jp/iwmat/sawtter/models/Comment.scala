package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

import jp.iwmat.sawtter.models.types.{ CommentText, URL }

case class Comment(
  commentId: Long,
  url: URL[Comment],
  userId: Long,
  text: CommentText[Comment],
  favorites: Long,
  createdAt: ZonedDateTime
)
