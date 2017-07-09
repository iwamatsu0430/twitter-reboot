package jp.iwmat.sawtter.controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.{ ExecutionContext, Future }

import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.services.AuthService

@Singleton
class AuthController @Inject() (
  service: AuthService
)(
  implicit
  val ec: ExecutionContext
) extends ControllerBase {

  implicit val reads = play.api.libs.json.Json.reads[SignUp]

  def signUp = Action.async(parse.json) { implicit req =>
    (for {
      signup <- deserializeT[SignUp, Future]
      _ <- service.signup(signup)
    } yield ()).toResult
  }

  def verify(token: String) = Action.async(parse.json) { implicit req =>
    service.verify(token).toResult { sessionKey =>
      Ok.withSession("session" -> sessionKey)
    }
  }
}
