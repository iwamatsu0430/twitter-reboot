package jp.iwmat.sawtter.controllers

import play.api.libs.json._
import play.api.mvc.{ Action => PlayAction, Controller, Request }
import scalaz.{ \/, EitherT, Monad }
import scalaz.std.FutureInstances

import jp.iwmat.sawtter.models.Errors
import jp.iwmat.sawtter.syntax.{ ResultOps, ToEitherOps }

trait ControllerBase
  extends Controller
  with ToEitherOps
  with ResultOps
  with FutureInstances {

  val Action = PlayAction

  val SecureAction = PlayAction // FIXME

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
