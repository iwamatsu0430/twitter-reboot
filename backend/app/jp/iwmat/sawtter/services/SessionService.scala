package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.{ ExecutionContext, Future }

import play.api.Configuration

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.repositories._

class SessionService @Inject() (
  sessionRepo: SessionRepository,
  conf: Configuration
)(
  implicit
  ec: ExecutionContext,
  rdb: RDB
) extends ServiceBase {

  def fetch()(implicit ctx: Option[User]): Result[User] = {
    Result(ctx \/> Errors.Unauthorized)
  }

  def findBy(sessionKey: String): Option[User] = {
    sessionRepo.fetch(sessionKey)
  }

  def delete()(implicit ctx: User): Unit = {
    sessionRepo.delete(ctx)
  }
}
