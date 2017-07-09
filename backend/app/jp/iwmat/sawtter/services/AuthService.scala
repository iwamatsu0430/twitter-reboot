package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.repositories._

class AuthService @Inject() (
  userRepository: UserRepository,
  sessionRepository: SessionRepository
)(
  implicit
  ec: ExecutionContext,
  rdb: RDB
) {

  def signup(signup: SignUp): Result[Unit] = {
    val result = for {
      userOpt <- userRepository.findBy(signup.email)
      _ <- DBResult.either(User.isValidForSignUpUser(userOpt)).or(Errors.signup.Exists(signup))
      userId <- userRepository.add(signup)
      // TODO send email
    } yield ()
    rdb.exec(result)
  }

  def verify(token: String): Result[String] = {
    val result = for {
      tokenOpt <- userRepository.findToken(token)
      token <- DBResult.getOrElse(tokenOpt)(Errors.signup.TokenNotFound(token))
      _ <- userRepository.enable(token.userId)
      userOpt <- userRepository.findBy(token.userId)
      user <- DBResult.getOrElse(userOpt)(Errors.Unexpected("User must be exists"))
      sessionKey = sessionRepository.add(user)
      // TODO send email
    } yield sessionKey
    rdb.exec(result)
  }
}
