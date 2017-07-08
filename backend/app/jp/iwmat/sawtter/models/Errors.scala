package jp.iwmat.sawtter.models

import play.api.libs.json.{ JsPath, JsError }
import scalaz.Monad
import scalaz.{ \/, EitherT }

sealed trait Errors {
  def code: String
  def message: String
}

object Errors {

  case class Unexpected(t: Throwable) extends Errors {
    val code = "error.unexpected"
    val message = s""
  }

  case class JsonError(error: JsError) extends Errors {
    val code = "error.json"
    val message = s""
  }

  case class EnumNotFound[A](key: A) extends Errors {
    val code = "error.enum"
    val message = s"Enum not found. key = $key"
  }

  object signup {
    case class Exists(signup: SignUp) extends Errors {
      val code = "error.signup.exists"
      val message = s"registed user is already exists. email=${signup.email}"
    }
  }

}
