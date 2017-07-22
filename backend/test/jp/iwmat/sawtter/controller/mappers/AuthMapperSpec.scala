package jp.iwmat.sawtter.controllers.mappers

import org.scalatest._
import play.api.libs.json.{ JsError, Json, JsSuccess }

import jp.iwmat.sawtter.models.SignUp
import jp.iwmat.sawtter.models.types.{ Email, Password }

class AuthMapperSpec extends WordSpec with MustMatchers with AuthMapper {
  "signupReads" must {

    val validEmail = "info@sawtter.iwmat.jp"
    val invalidEmail = ""
    val validPassword = "000001aA"
    val invalidPassword = ""

    "can convert valid email, valid password" in {
      Json.parse(s"""{
        "email": "$validEmail",
        "password": "$validPassword"
      }""").validate[SignUp] mustBe JsSuccess(SignUp(Email(validEmail), Password(validPassword)))
    }

    "cannot convert invalid email, valid password" in {
      Json.parse(s"""{
        "email": "$invalidEmail",
        "password": "$validPassword"
      }""").validate[SignUp].isError mustBe true
    }

    "cannot convert valid email, invalid password" in {
      Json.parse(s"""{
        "email": "$validEmail",
        "password": "$invalidPassword"
      }""").validate[SignUp].isError mustBe true
    }

    "cannot convert invalid email, invalid password" in {
      Json.parse(s"""{
        "email": "$invalidEmail",
        "password": "$invalidPassword"
      }""").validate[SignUp].isError mustBe true
    }
  }
}
