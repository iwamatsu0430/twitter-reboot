package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import play.api.Configuration

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.repositories._

class SessionService @Inject() (
  userRepository: UserRepository,
  sessionRepository: SessionRepository,
  conf: Configuration
)(
  implicit
  ec: ExecutionContext,
  rdb: RDB
) {
  def findBy(sessionKey: String): Result[Option[User]] = ???
}
