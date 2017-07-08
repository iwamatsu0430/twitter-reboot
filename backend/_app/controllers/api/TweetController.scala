package controllers.api

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.functional.syntax._

import actions.AuthAction
import actions.AuthAction.getSessionMember
import controllers.ResponseCode._
import models.{ShareContentsModel, TweetModel}
import utils.JsonUtil._

case class PostedTweet(url: String, comment: String)

/**
  * @author SAW
 */
class TweetController extends Controller {

  implicit val tweetReads: Reads[PostedTweet] = (
    (JsPath \ "url").read[String] and
    (JsPath \ "comment").read[String](minLength[String](1) keepAnd maxLength[String](140))
  )(PostedTweet.apply _)

  def tweet = AuthAction.async(parse.json) { request =>
    request.body.validate[PostedTweet] match {
      case JsSuccess(value, path) => {
        for {
          loginMember   <- getSessionMember(request)
          shareContents <- ShareContentsModel.createOrFind(value.url)
          tweetId       <- TweetModel.tweet(loginMember.memberId, value.url, value.comment, shareContents)
        } yield {
          Ok(createJson(NoReason, tweetId))
        }
      }
      case e: JsError => Future.successful(BadRequest(createJson(ValidationError, JsError.toJson(e))))
    }
  }

  def delete(tweetId: String) = AuthAction.async { request =>
    for {
      loginMember   <- getSessionMember(request)
      result        <- TweetModel.findById(tweetId).map {
        case None => BadRequest(createJson(TweetNotFound))
        case targetTweet if targetTweet.get.memberId != loginMember.memberId => BadRequest(createJson(TweetIsNotYours))
        case targetTweet if targetTweet.get.deleted => NotFound(createJson(TweetNotFound))
        case targetTweet => {
          TweetModel.delete(targetTweet.get)
          Ok(successJson)
        }
      }
    } yield result
  }
}
