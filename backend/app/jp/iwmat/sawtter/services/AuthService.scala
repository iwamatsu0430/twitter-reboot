package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import play.api.Configuration

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.repositories._

class AuthService @Inject() (
  userRepository: UserRepository,
  sessionRepository: SessionRepository,
  mail: Mail,
  conf: Configuration
)(
  implicit
  ec: ExecutionContext,
  rdb: RDB
) {

  def signup(signup: SignUp): Result[Unit] = {
    val result = for {
      userOpt <- userRepository.findBy(signup.email)
      _ <- DBResult.either(User.isValidForSignUpUser(userOpt)).or(Errors.signup.Exists(signup))
      token <- userRepository.add(signup)
      mailData = MailData(
        signup.email,
        "info@sawtter.iwmat.jp",
        "SAWTTERへようこそ！",
        s"SAWTTERへようこそ！\n\n登録を完了するために以下のリンクをクリックしてください！\n${conf.getString("sawtter.hosts.backend").getOrElse("")}/api/auth/verify/${token.token}"
      )
      _ = mail.send(mailData)
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
