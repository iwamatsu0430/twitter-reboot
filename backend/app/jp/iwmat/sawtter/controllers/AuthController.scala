package jp.iwmat.sawtter.controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.{ ExecutionContext, Future }

import jp.iwmat.sawtter.controllers.mappers.AuthMapper
import jp.iwmat.sawtter.models.configurations.SawtterConf
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.services.{ AuthService, SessionService }

@Singleton
class AuthController @Inject() (
  sawtterConf: SawtterConf,
  authService: AuthService,
  val sessionService: SessionService
)(
  implicit
  val ec: ExecutionContext
) extends ControllerBase with AuthMapper {

  def signUp = Action.async(parse.json) { implicit req =>
    (for {
      payload <- deserializeT[SignUp, Future]
      _ <- authService.signup(payload)
    } yield ()).toResult
  }

  def verify(token: String) = Action.async { implicit req =>
    authService.verify(token).toResult { sessionKey =>
      Redirect(sawtterConf.hosts.frontend)
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
