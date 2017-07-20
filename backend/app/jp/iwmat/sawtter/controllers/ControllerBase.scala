package jp.iwmat.sawtter.controllers

import scala.concurrent.{ ExecutionContext, Future }

import play.api.libs.json._
import play.api.mvc.{ Action => PlayAction, Controller, Request }
import scalaz.{ \/, \/-, EitherT, Monad }
import scalaz.std.FutureInstances

import jp.iwmat.sawtter.models.Errors
import jp.iwmat.sawtter.services.SessionService
import jp.iwmat.sawtter.syntax.{ ResultOps, ToEitherOps }

trait ControllerBase
  extends Controller
  with ToEitherOps
  with ResultOps
  with FutureInstances {

  def sessionService: SessionService

  implicit val ec: ExecutionContext

  val Action = PlayAction

  val SecureAction = PlayAction // FIXME

  import play.api.mvc.{ ActionBuilder, Result, Request }
  object PublicAction extends ActionBuilder[Request] {
    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      request.session.get("session") match {
        case Some(sessionKey) => sessionService.findBy(sessionKey).run.flatMap {
          case \/-(Some(user)) => ???
          case _ => ???
        }
        case None => ???
      }
      block.apply(request)
    }
  }

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
    deserialize(req, reads, monad).et
  }

  implicit val unitWrites: Writes[Unit] = new Writes[Unit] {
    def writes(value: Unit): JsValue = JsNull
  }
}
