package jp.iwmat.sawtter.repositories.mappers

import java.time.ZonedDateTime

import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._
import jp.iwmat.sawtter.models.Enum.writes._

trait SessionMapper extends TypeReads with TypeWrites {

  implicit def userStatusReads: Reads[UserStatus] = new Reads[UserStatus] {
    def reads(json: JsValue): JsResult[UserStatus] = {
      for {
        value <- json.validate[String]
        result <- UserStatus.values.find(_.value == value) match {
          case Some(userStatus) => JsSuccess(userStatus)
          case None => JsError(JsPath \ "status", s"Unknown status. status=$value")
        }
      } yield result
    }
  }

  implicit def userReads: Reads[User] = (
    (__ \ "userId").read[ID[User]] and
    (__ \ "email").read[Email[User]] and
    (__ \ "status").read[UserStatus] and
    (__ \ "version").read[Version[User]] and
    (__ \ "updatedAt").read[ZonedDateTime] and
    (__ \ "createdAt").read[ZonedDateTime]
  )(User.apply _)

  implicit def userWrites: Writes[User] = (
    (__ \ "userId").write[ID[User]] and
    (__ \ "email").write[Email[User]] and
    (__ \ "status").write[UserStatus] and
    (__ \ "version").write[Version[User]] and
    (__ \ "updatedAt").write[ZonedDateTime] and
    (__ \ "createdAt").write[ZonedDateTime]
  )(unlift(User.unapply))
}
