package jp.iwmat.sawtter.controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.{ ExecutionContext, Future }

import play.api.Configuration

import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.services.{ AuthService, SessionService }

@Singleton
class AuthController @Inject() (
  conf: Configuration,
  authService: AuthService,
  val sessionService: SessionService
)(
  implicit
  val ec: ExecutionContext
) extends ControllerBase {

  implicit val signupReads = play.api.libs.json.Json.reads[SignUp]
  implicit val loginReads = play.api.libs.json.Json.reads[Login]

  def signUp = Action.async(parse.json) { implicit req =>
    (for {
      payload <- deserializeT[SignUp, Future]
      _ <- authService.signup(payload)
    } yield ()).toResult
  }

  def verify(token: String) = Action.async { implicit req =>
    authService.verify(token).toResult { sessionKey =>
      Redirect(conf.getString("sawtter.hosts.frontend").getOrElse("")) // FIXME
    }
  }

  def login = Action.async(parse.json) { implicit req =>
    (for {
      payload <- deserializeT[Login, Future]
      sessionKey <- authService.login(payload)
    } yield sessionKey).toResult { sessionKey =>
      Ok.withSession("session" -> sessionKey)
    }
  }

  def logout = SecureAction { implicit req =>
    sessionService.delete()
    Ok.withNewSession
  }
}
