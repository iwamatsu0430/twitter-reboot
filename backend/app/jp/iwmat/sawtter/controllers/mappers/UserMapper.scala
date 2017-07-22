package jp.iwmat.sawtter.controllers.mappers

import play.api.libs.json._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._

trait UserMapper extends MapperBase {
  implicit val userWrites: Writes[User] = new Writes[User] {
    def writes(user: User): JsValue = Json.obj(
      "userId" -> JsString(user.userId.value.toString),
      "email" -> JsString(user.email.value)
    )
  }
}
