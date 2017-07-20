package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.{ ExecutionContext, Future }

import play.api.Configuration
import scalaz.syntax.std.ToOptionOps

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
) extends ToOptionOps {

  def fetch()(implicit ctx: Option[User]): Result[User] = {
    Result(ctx \/> Errors.Unauthorized)
  }

  def findBy(sessionKey: String): Option[User] = {
    sessionRepository.fetch(sessionKey)
  }

  def delete()(implicit ctx: User): Unit = {
    sessionRepository.delete(ctx)
  }
}
