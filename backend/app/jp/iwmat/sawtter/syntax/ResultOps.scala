package jp.iwmat.sawtter.syntax

import scala.concurrent.{ ExecutionContext, Future }

import play.api.libs.json.{ Json, Writes }
import play.api.http.Status
import play.api.mvc.{ Result => PResult, Results }
import scalaz.{ -\/, \/- }

import jp.iwmat.sawtter.{ Result => SResult }
import jp.iwmat.sawtter.models.Errors

trait ResultOps extends Results with Status {

  implicit class ErrorsToOps(errors: Errors) {
    def toResult: PResult = errors match {
      // FIXME
      case Errors.Unauthorized => Unauthorized(Json.obj(
        "code" -> Errors.Unauthorized.code,
        "message" -> Errors.Unauthorized.message
      ))
      case _ => BadRequest(Json.obj(
        "code" -> errors.code,
        "message" -> errors.message
      ))
    }
  }

  implicit class ResultToResultOps[A](result: SResult[A]) {
    def toResult(implicit ec: ExecutionContext, writes: Writes[A]): Future[PResult] = {
      toResult { value =>
        Ok(Json.toJson(value))
      }
    }

    def toResult(f: A => PResult)(implicit ec: ExecutionContext): Future[PResult] = {
      result.run
        .recover {
          case t =>
            -\/(Errors.Unexpected(t))
        }
        .map {
          case \/-(a) => f(a)
          case -\/(e) => e.toResult
        }
    }
  }
}
