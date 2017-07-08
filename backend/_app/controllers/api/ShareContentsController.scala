package controllers.api

import actions.AuthAction._
import models.{ShareContentsDetail, TweetModel, ShareContentsModel}
import play.api.mvc.{Action, Controller}
import utils.JsonUtil._
import controllers.ResponseCode._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author SAW
 */
class ShareContentsController extends Controller {

  def detail(shareContentsId: String) = Action.async { request =>
    for {
      shareContents   <- ShareContentsModel.findById(shareContentsId)
      loginMemberOpt  <- getSessionMemberOpt(request)
      tweetIds        <- TweetModel.findByShareContentsIds(shareContentsId)
      json            <- ShareContentsDetail(shareContents, tweetIds, loginMemberOpt).toJson
    } yield {
      Ok(createJson(NoReason, json))
    }
  }
}
