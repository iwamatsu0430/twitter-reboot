package jp.iwmat.sawtter.controllers

import play.api.libs.json._
import scalaz.\/-

import jp.iwmat.sawtter.base.DBSpecBase
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._
import jp.iwmat.sawtter.repositories._

class AuthControllerSpec extends DBSpecBase with AuthControllerSpecAction {

  val userRepository = app.injector.instanceOf[UserRepository]

  "signup" must {
    "can signup by valid email and valid password" in {
      val email = Email[SignUp]("signup1@test.com")
      val result = signUp(email.value, "1Password").await()

      result.header.status mustBe 200
      val user = execDB(userRepository.findBy(email))
      user.map(_.isEmpty) mustBe \/-(false)
      user.map(_.map(_.email)) mustBe \/-(Some(email))
    }

    "can not signup by exists email and valid password" in {
      val result = signUp("signup1@test.com", "1Password").await()
      result.header.status mustBe 400
    }

    "can not signup by invalid email and valid password" in {
      val result = signUp("signup", "1Password").await()
      result.header.status mustBe 400
    }

    "can not signup by valid email and invalid password" in {
      val result = signUp("signup2@test.com", "password").await()
      result.header.status mustBe 400
    }

    "can not signup by invalid email and invalid password" in {
      val result = signUp("signup", "password").await()
      result.header.status mustBe 400
    }

    "can not signup by valid email and no password" in {
      val result = signUp(Json.obj("email" -> "signup2@test.com")).await()
      result.header.status mustBe 400
    }

    "can not signup by no email and valid password" in {
      val result = signUp(Json.obj("password" -> "1Password")).await()
      result.header.status mustBe 400
    }

    "can not signup by empty json" in {
      val result = signUp(Json.obj()).await()
      result.header.status mustBe 400
    }
  }
}

trait AuthControllerSpecAction {

  import scala.concurrent.Future

  import play.api.Application
  import play.api.mvc.Result
  import play.api.test._

  implicit def app: Application

  val controller = app.injector.instanceOf[AuthController]

  def signUp(json: JsValue): Future[Result] = {
    val request = FakeRequest(
      "POST",
      "/api/auth/signup",
      FakeHeaders(),
      json
    )
    controller.signUp().apply(request)
  }

  def signUp(email: String, password: String): Future[Result] = {
    signUp(Json.obj(
      "email" -> email,
      "password" -> password
    ))
  }

}
