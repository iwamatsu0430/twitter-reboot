package jp.iwmat.sawtter.infrastructure.cache._shade

import javax.inject.Inject

import scala.concurrent.duration._

import jp.iwmat.sawtter.infrastructure.cache._shade.mappers.SessionMapper
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.repositories.SessionRepository
import jp.iwmat.sawtter.utils.generators.{ Clocker, IdentifyBuilder, Security }

class SessionRepositoryShade @Inject()(
  cache: ShadeCache,
  identifyBuilder: IdentifyBuilder,
  security: Security,
  clocker: Clocker
) extends SessionRepository with SessionMapper {

  def fetch(key: String): Option[User] = {
    cache.getJson[User](key)
  }

  def add(user: User): String = {
    val key = security.encrypt(user.userId.toString)
    cache.setJson(key, user, 24 hour)
    key
  }

  def delete(user: User): Unit = {
    val key = security.encrypt(user.userId.toString)
    cache.delete(key)
  }
}
