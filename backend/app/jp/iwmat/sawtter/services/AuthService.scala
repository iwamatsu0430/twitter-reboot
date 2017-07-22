package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import play.api.Configuration

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.configurations._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.mails._
import jp.iwmat.sawtter.repositories._

class AuthService @Inject() (
  userRepository: UserRepository,
  sessionRepository: SessionRepository,
  mailer: Mailer,
  sawtterConf: SawtterConfiguration
)(
  implicit
  ec: ExecutionContext,
  rdb: RDB
) {

  def signup(signup: SignUp): Result[Unit] = {
    val result = for {
      userOpt <- userRepository.findBy(signup.email)
      _ <- DBResult.either(User.isValidForSignUp(userOpt)) or Errors.signup.Exists(signup)
      token <- userRepository.add(signup)
      mail = SignUpMail(signup.email, sawtterConf.domain, sawtterConf.hosts.backend, token.token)
      _ = mailer.send(mail)
    } yield ()
    rdb.exec(result)
  }

  def verify(token: String): Result[String] = {
    val result = for {
      tokenOpt <- userRepository.findToken(token)
      token <- DBResult.getOrElse(tokenOpt)(Errors.signup.TokenNotFound(token))
      userOpt <- userRepository.findBy(token.userId)
      user <- DBResult.getOrElse(userOpt)(Errors.Unexpected("User must be exists"))
      _ <- userRepository.enable(user)
      sessionKey = sessionRepository.add(user)
    } yield sessionKey
    rdb.exec(result)
  }

  def login(login: Login): Result[String] = {
    val result = for {
      userOpt <- userRepository.findBy(login)
      user <- DBResult.getOrElse(userOpt)(Errors.login.NotFound(login))
      sessionKey = sessionRepository.add(user)
    } yield sessionKey
    rdb.exec(result)
  }
}
