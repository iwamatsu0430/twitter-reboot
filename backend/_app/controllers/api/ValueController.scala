package controllers.api

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc.{Action, Controller}

import actions.AuthAction
import actions.AuthAction.getSessionMember
import controllers.ResponseCode._
import models.TweetModel
import models._
import utils.JsonUtil._

/**
 * @author SAW
 */
class ValueController extends Controller {

  def count(tweetId: String) = Action.async { request =>
    TweetModel.findById(tweetId).flatMap {
      case None => Future.successful(NotFound(createJson(TweetNotFound)))
      case Some(tweet) => getSessionMember(request).flatMap { loginMember =>
        ValueModel.countValueByTweet(tweet, Some(loginMember)).map { valueCount =>
          Ok(createJson(NoReason, valueCount.toJson))
        }
      }
    }
  }

  def good(tweetId: String) = AuthAction.async { request =>
    TweetModel.findById(tweetId).flatMap {
      case None => Future.successful(NotFound(createJson(TweetNotFound)))
      case Some(tweet) => getSessionMember(request).flatMap { loginMember =>
        ValueModel.existsValued(loginMember, tweet).flatMap {
          case true => Future.successful(BadRequest(createJson(AlreadyValued)))
          case false => {
            ValueModel.good(loginMember, tweet)
            Future.successful(Ok(createJson(NoReason)))
          }
        }
      }
    }
  }

  def bad(tweetId: String) = AuthAction.async { request =>
    TweetModel.findById(tweetId).flatMap {
      case None => Future.successful(NotFound(createJson(TweetNotFound)))
      case Some(tweet) => getSessionMember(request).flatMap { loginMember =>
        ValueModel.existsValued(loginMember, tweet).flatMap {
          case true => Future.successful(BadRequest(createJson(AlreadyValued)))
          case false => {
            ValueModel.bad(loginMember, tweet)
            Future.successful(Ok(createJson(NoReason)))
          }
        }
      }
    }
  }

  def cancel(tweetId: String) = AuthAction.async { request =>
    TweetModel.findById(tweetId).flatMap {
      case None => Future.successful(NotFound(createJson(TweetNotFound)))
      case Some(tweet) => getSessionMember(request).flatMap { loginMember =>
        ValueModel.existsValued(loginMember, tweet).flatMap {
          case false => Future.successful(BadRequest(createJson(AlreadyValued)))
          case true => {
            ValueModel.cancel(loginMember, tweet)
            Future.successful(Ok(createJson(NoReason)))
          }
        }
      }
    }
  }
}
