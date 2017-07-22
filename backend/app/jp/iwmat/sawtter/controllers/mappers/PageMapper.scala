package jp.iwmat.sawtter.controllers.mappers

import play.api.libs.json._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._

trait PageMapper extends MapperBase {

  implicit val newCommentReads: Reads[NewComment] = new Reads[NewComment] {
    def reads(json: JsValue): JsResult[NewComment] = {
      for {
        comment <- (json \ "text").validate[CommentText[NewComment]]
        _ <- if (comment.isValid) JsSuccess(()) else JsError(JsPath \ "text", "invalid format")
      } yield NewComment(comment)
    }
  }

  implicit val commentWrites: Writes[Comment] = new Writes[Comment] {
    def writes(comment: Comment): JsValue = Json.obj(
      "commentId" -> JsString(comment.commentId.toString),
      "text" -> Json.toJson(comment.text),
      "favorites" -> JsNumber(comment.favorites),
      "createdAt" -> Json.toJson(comment.createdAt)
    )
  }
}
