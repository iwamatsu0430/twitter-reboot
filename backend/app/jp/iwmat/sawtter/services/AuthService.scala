package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.configurations._
import jp.iwmat.sawtter.models.mails._
import jp.iwmat.sawtter.repositories._
import jp.iwmat.sawtter.utils.Mailer

class AuthService @Inject() (
  val userRepo: UserRepository,
  val sessionRepo: SessionRepository,
  val mailer: Mailer,
  sawtterConf: SawtterConf
)(
  implicit
  val ec: ExecutionContext,
  val rdb: RDB
) extends ServiceBase {

  def signup(signup: SignUp): Result[Unit] = {
    val transaction = for {
      userOpt <- userRepo.findBy(signup.email)
      _ <- User.isValidForSignUp(userOpt) orElse AuthServiceErrors.userNotExists(signup)
      token <- userRepo.add(signup)
      mail = SignUpMail.create(signup.email, token.token, sawtterConf)
      _ = mailer.send(mail)
    } yield ()
    transaction.execute()
  }

  def verify(tokenString: String): Result[String] = {
    val transaction = for {
      tokenOpt <- userRepo.findToken(tokenString)
      token <- tokenOpt getOr AuthServiceErrors.tokenNotFound(tokenString)
      userOpt <- userRepo.findBy(token.userId)
      user <- userOpt getOr AuthServiceErrors.userMustExists
      _ <- userRepo.enable(user)
      sessionKey = sessionRepo.add(user)
    } yield sessionKey
    transaction.execute()
  }

  def login(login: Login): Result[String] = {
    val transaction = for {
      userOpt <- userRepo.findBy(login)
      user <- userOpt getOr AuthServiceErrors.userNotFound(login)
      sessionKey = sessionRepo.add(user)
    } yield sessionKey
    transaction.execute()
  }
}

object AuthServiceErrors {
  def userNotExists(signup: SignUp) = Errors.signup.Exists(signup)

  def tokenNotFound(token: String) = Errors.signup.TokenNotFound(token)

  def userMustExists = Errors.Unexpected("User must be exists")

  def userNotFound(login: Login) = Errors.login.NotFound(login)
}
