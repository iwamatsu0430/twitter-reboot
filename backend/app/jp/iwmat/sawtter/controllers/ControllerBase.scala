package jp.iwmat.sawtter.controllers

import scala.concurrent.{ ExecutionContext, Future }

import play.api.libs.json._
import play.api.mvc.{ Action => PlayAction, _ }
import scalaz.{ \/, \/-, EitherT, Monad }
import scalaz.std.FutureInstances

import jp.iwmat.sawtter.controllers.mappers.MapperBase
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.services.SessionService
import jp.iwmat.sawtter.syntax.{ ResultOps, ToEitherOps }

trait ControllerBase
  extends Controller
  with MapperBase
  with ToEitherOps
  with ResultOps
  with FutureInstances {

  def sessionService: SessionService

  implicit val ec: ExecutionContext

  val Action = PlayAction

  case class PublicRequest[A](
    user: Option[User],
    request: Request[A]
  ) extends WrappedRequest[A](request)

  implicit def publicRequest2User(implicit request: PublicRequest[_]): Option[User] = request.user

  object PublicAction {
    def apply(requestHandler: PublicRequest[AnyContent] => Result): PlayAction[AnyContent] = {
      apply(BodyParsers.parse.anyContent)(requestHandler)
    }

    def apply[A](bodyParser: BodyParser[A])(requestHandler: PublicRequest[A] => Result): PlayAction[A] = {
      PlayAction(bodyParser) { req =>
        requestHandler(PublicRequest(findUserBySession(req), req))
      }
    }

    def async(requestHandler: PublicRequest[AnyContent] => Future[Result]): PlayAction[AnyContent] = {
      async(BodyParsers.parse.anyContent)(requestHandler)
    }

    def async[A](bodyParser: BodyParser[A])(requestHandler: PublicRequest[A] => Future[Result]): PlayAction[A] = {
      PlayAction.async(bodyParser) { req =>
        requestHandler(PublicRequest(findUserBySession(req), req))
      }
    }
  }

  case class SecureRequest[A](
    user: User,
    request: Request[A]
  ) extends WrappedRequest[A](request)

  implicit def secureRequest2User(implicit request: SecureRequest[_]): User = request.user

  object SecureAction {
    def apply(requestHandler: SecureRequest[AnyContent] => Result): PlayAction[AnyContent] = {
      apply(BodyParsers.parse.anyContent)(requestHandler)
    }

    def apply[A](bodyParser: BodyParser[A])(requestHandler: SecureRequest[A] => Result): PlayAction[A] = {
      PlayAction(bodyParser) { req =>
        findUserBySession(req) match {
          case Some(user) => requestHandler(SecureRequest(user, req))
          case _ => Errors.Unauthorized.toResult
        }
      }
    }

    def async(requestHandler: SecureRequest[AnyContent] => Future[Result]): PlayAction[AnyContent] = {
      async(BodyParsers.parse.anyContent)(requestHandler)
    }

    def async[A](bodyParser: BodyParser[A])(requestHandler: SecureRequest[A] => Future[Result]): PlayAction[A] = {
      PlayAction.async(bodyParser) { req =>
        findUserBySession(req) match {
          case Some(user) => requestHandler(SecureRequest(user, req))
          case _ => Future.successful(Errors.Unauthorized.toResult)
        }
      }
    }
  }

  def findUserBySession(req: Request[_]): Option[User] = {
    for {
      key <- req.session.get("session")
      user <- sessionService.findBy(key)
    } yield user
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
}
