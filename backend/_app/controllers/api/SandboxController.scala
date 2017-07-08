package controllers.api

import actions.AuthAction._
import controllers.ResponseCode._
import models.{TimelineModel}
import play.api.mvc.{Action, Controller}
import actions.AuthAction
import utils.JsonUtil._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author SAW
 */
class SandboxController extends Controller {

  def get = Action.async {
    request =>
      // get search params
      val beforeTimestamp = request.getQueryString("before").map(_.toLong).getOrElse(Long.MaxValue)
      val afterTimestamp = request.getQueryString("after").map(_.toLong).getOrElse(Long.MinValue)

      // find timeline
      getSessionMemberOpt(request).flatMap { loginMemberOpt =>
        TimelineModel.findAll(loginMemberOpt, beforeTimestamp, afterTimestamp).map{ timeline =>
          Ok(createJson(NoReason, timeline.map(_.toJson)))
        }
      }
  }

  def auth = AuthAction {
    Ok
  }

  def post = Action(parse.json) { implicit request =>
    Ok
  }

  def delete = Action {
    Ok
  }

  def front = Action {
    Ok(views.html.sandbox.render)
  }
}
