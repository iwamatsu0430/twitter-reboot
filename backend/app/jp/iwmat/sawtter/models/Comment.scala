package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

import jp.iwmat.sawtter.models.types.{ CommentText, ID, URL }

case class Comment(
  commentId: ID[Comment],
  url: URL[Comment],
  userId: ID[User],
  text: CommentText[Comment],
  favorites: Long,
  createdAt: ZonedDateTime
)
