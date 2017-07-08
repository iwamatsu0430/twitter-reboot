package jp.iwmat.sawtter.syntax

import scala.concurrent.{ ExecutionContext, Future }

import play.api.libs.json.{ Json, Writes }
import play.api.mvc.{ Result => PResult, Results }
import scalaz.{ -\/, \/- }

import jp.iwmat.sawtter.{ Result => SResult }
import jp.iwmat.sawtter.models.Errors

trait ResultOps extends Results {

  implicit class ErrorsToOps(errors: Errors) {
    def toResult: PResult = errors match {
      // FIXME
      case _ => BadRequest(Json.obj(
        "code" -> errors.code,
        "message" -> errors.message
      ))
    }
  }

  implicit class ResultToResultOps[A](result: SResult[A]) {
    def toResult(implicit ec: ExecutionContext, writes: Writes[A]): Future[PResult] = {
      result.run
        .recover {
          case t =>
            println(t)
            -\/(Errors.Unexpected(t))
        }
        .map {
          case \/-(a) => Ok(Json.toJson(a))
          case -\/(e) => e.toResult
        }
    }
  }
}
