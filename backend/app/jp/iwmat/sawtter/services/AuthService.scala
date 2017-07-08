package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.repositories._

class AuthService @Inject() (
  userRepository: UserRepository
)(
  implicit
  ec: ExecutionContext,
  rdb: RDB
) {

  def existsError(signup: SignUp) = Errors.signup.Exists(signup)

  def signup(signup: SignUp): Result[Unit] = {
    val result = for {
      userOpt <- userRepository.findBy(signup.email)
      _ <- DBResult.either(userOpt.isEmpty).or(existsError(signup))
      userId <- userRepository.add(signup)
      token <- userRepository.addToken(userId)
      // send mail
    } yield ()
    rdb.exec(result)
  }
}
