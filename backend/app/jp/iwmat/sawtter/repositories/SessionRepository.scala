package jp.iwmat.sawtter.repositories

import javax.inject.Inject

import scala.concurrent.duration._

import jp.iwmat.sawtter.generators.{ Clocker, IdentifyBuilder, Security }
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.repositories.mappers.SessionMapper

class SessionRepository @Inject()(
  cache: Cache,
  identifyBuilder: IdentifyBuilder,
  security: Security,
  clocker: Clocker
) extends SessionMapper {

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
