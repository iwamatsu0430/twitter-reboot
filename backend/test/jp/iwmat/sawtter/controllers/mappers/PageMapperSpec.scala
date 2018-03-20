package jp.iwmat.sawtter.controllers.mappers

import java.time.{ ZonedDateTime, ZoneOffset }

import org.scalatest._
import play.api.libs.json.{ JsError, Json, JsSuccess }

import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._

class PageMapperSpec extends WordSpec with MustMatchers with PageMapper {

  val validText = "hello";
  val invalidText = "";

  "newCommentReads" must {
    "can convert valid text" in {
      Json.parse(s"""{
        "text": "$validText"
      }""").validate[NewComment].isSuccess mustBe true
    }

    "cannot convert invalid text" in {
      Json.parse(s"""{
        "text": "$invalidText"
      }""").validate[NewComment].isError mustBe true
    }

    "cannot convert no text" in {
      Json.parse(s"""{}""").validate[NewComment].isError mustBe true
    }
  }

  "commentWrites" must {
    "can convert with writes" in {
      val comment = Comment(ID(1L), URL("url"), ID(1L), CommentText("hello"), 1L, ZonedDateTime.of(2017, 7, 21, 0, 0, 0, 0, ZoneOffset.UTC))
      Json.toJson(comment) mustBe Json.obj(
        "commentId" -> "1",
        "text" -> "hello",
        "favorites" -> 1L,
        "createdAt" -> "2017-07-21T00:00:00Z"
      )
    }
  }
}
