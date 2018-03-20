package jp.iwmat.sawtter.controllers

import scala.concurrent.{ ExecutionContext, Future }

import play.api.libs.json._
import play.api.mvc.{ Action => PlayAction, _ }
import scalaz.{ \/, \/-, EitherT, Monad }
import scalaz.std.FutureInstances

import jp.iwmat.sawtter.controllers.mappers.MapperBase
import jp.iwmat.sawtter.controllers.secure._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.services.SessionService
import jp.iwmat.sawtter.utils.syntax.ResultOps

trait ControllerBase
  extends Controller
  with MapperBase
  with ResultOps
  with FutureInstances { self =>

  def sessionService: SessionService

  implicit val ec: ExecutionContext

  val Action = PlayAction
  val PublicAction = new PublicAction { def sessionService = self.sessionService }
  val SecureAction = new SecureAction { def sessionService = self.sessionService }

  implicit def publicRequest2User(implicit request: PublicRequest[_]): Option[User] = request.user
  implicit def secureRequest2User(implicit request: SecureRequest[_]): User = request.user

  def deserialize[A](implicit req: Request[JsValue], reads: Reads[A]): Errors \/ A = {
    req.body.validate[A] match {
      case e: JsError => \/.left(Errors.JsonError(e))
      case s: JsSuccess[A] => \/.right(s.value)
    }
  }

  def deserialize[A, F[_]](implicit req: Request[JsValue], reads: Reads[A], monad: Monad[F]): F[Errors \/ A] = {
    val either = req.body.validate[A] match {
      case e: JsError => \/.left(Errors.JsonError(e))
      case s: JsSuccess[A] => \/.right(s.value)
    }
    monad.point(either)
  }

  def deserializeT[A, F[_]](implicit req: Request[JsValue], reads: Reads[A], monad: Monad[F]): EitherT[F, Errors, A] = {
    EitherT(deserialize(req, reads, monad))
  }
}
