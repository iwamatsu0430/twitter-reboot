package jp.iwmat.sawtter.models

import play.api.libs.json.{ JsPath, JsError }
import scalaz.Monad
import scalaz.{ \/, EitherT }

sealed trait Errors {
  def code: String
  def message: String
}

object Errors {

  trait Unexpected extends Errors
  object Unexpected {
    def apply(t: Throwable): Unexpected = new Unexpected {
      val code = "error.unexpected"
      val message = s"Unexpected error occured. error=$t, ${t.getStackTrace.mkString("\n")}" // FIXME
    }

    def apply(msg: String): Unexpected = new Unexpected {
      val code = "error.unexpected"
      val message = msg
    }
  }

  case class JsonError(error: JsError) extends Errors {
    val code = "error.json"
    val message = s"Json error occured. error=$error"
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

    case class TokenNotFound(token: String) extends Errors {
      val code = "error.signup.tokenNotFound"
      val message = s"token not found. token=${token}"
    }
  }

}
