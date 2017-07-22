package jp.iwmat.sawtter.models.types

import jp.iwmat.sawtter.models.Iso

case class CommentText[A](value: String) extends AnyVal {
  def isValid = CommentText.isValid(this)
}

object CommentText {

  implicit def iso[A]: Iso[String, CommentText[A]] = new Iso[String, CommentText[A]] {
    def to(a: String): CommentText[A] = CommentText(a)
    def from(b: CommentText[A]): String = b.value
  }

  /**
    * Validate comment text.
    * - comment text must be 1 ~ 140 chars
    */
  def isValid(comment: CommentText[_]): Boolean = {
    comment.value.length > 0 && comment.value.length <= 140
  }
}
