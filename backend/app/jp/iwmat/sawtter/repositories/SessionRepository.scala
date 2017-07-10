package jp.iwmat.sawtter.repositories

import javax.inject.Inject

import scala.concurrent.duration._

import play.api.libs.json._

import jp.iwmat.sawtter.generators.{ Clocker, IdentifyBuilder, Security }
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.Enum.writes._

class SessionRepository @Inject()(
  cache: Cache,
  identifyBuilder: IdentifyBuilder,
  security: Security,
  clocker: Clocker
) {

  implicit def userWrites: Writes[User] = Json.writes[User]

  def add(user: User): String = {
    val key = security.encrypt(user.userId.toString)
    cache.setJson(key, user, 24 hour)
    key
  }
}
