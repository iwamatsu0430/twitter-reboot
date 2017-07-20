package jp.iwmat.sawtter.repositories

import javax.inject.Inject

import scala.concurrent.duration._

import play.api.data.validation.ValidationError
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

  implicit def userStatusReads: Reads[UserStatus] = new Reads[UserStatus] {
    def reads(json: JsValue): JsResult[UserStatus] = {
      for {
        value <- json.validate[String]
        result <- UserStatus.values.find(_.value == value) match {
          case Some(userStatus) => JsSuccess(userStatus)
          case None => JsError(ValidationError("")) // FIXME
        }
      } yield result
    }
  }
  implicit def userReads: Reads[User] = Json.reads[User]
  implicit def userWrites: Writes[User] = Json.writes[User]

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
