package jp.iwmat.sawtter.controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.{ ExecutionContext, Future }

import play.api.libs.json.Json


import jp.iwmat.sawtter.models.Enum.writes._
import jp.iwmat.sawtter.models.User
import jp.iwmat.sawtter.services.SessionService

@Singleton
class UserController @Inject() (
  val sessionService: SessionService
)(
  implicit
  val ec: ExecutionContext
) extends ControllerBase {

  implicit val userWrites = Json.writes[User]

  def me = PublicAction.async { implicit req =>
    sessionService.fetch().toResult
  }
}
