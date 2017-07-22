package jp.iwmat.sawtter.controllers.mappers

import play.api.libs.json._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._

trait AuthMapper extends MapperBase {

  implicit def signupReads = new Reads[SignUp] {
    def reads(json: JsValue): JsResult[SignUp] = {
      for {
        email <- (json \ "email").validate[Email[SignUp]]
        _ <- if (email.isValid) JsSuccess(()) else JsError(JsPath \ "email", "invalid format")
        password <- (json \ "password").validate[Password[SignUp]]
        _ <- if (password.isValid) JsSuccess(()) else JsError(JsPath \ "password", "invalid format")
      } yield SignUp(email, password)
    }
  }

  implicit def loginReads = new Reads[Login] {
    def reads(json: JsValue): JsResult[Login] = {
      for {
        email <- (json \ "email").validate[Email[Login]]
        _ <- if (email.isValid) JsSuccess(()) else JsError(JsPath \ "email", "invalid format")
        password <- (json \ "password").validate[Password[Login]]
        _ <- if (password.isValid) JsSuccess(()) else JsError(JsPath \ "password", "invalid format")
      } yield Login(email, password)
    }
  }
}
