package jp.iwmat.sawtter.controllers.secure

import scala.concurrent.Future

import play.api.mvc._

import jp.iwmat.sawtter.models.Errors
import jp.iwmat.sawtter.syntax.ResultOps

trait SecureAction extends ActionBase with ResultOps {
  def apply(requestHandler: SecureRequest[AnyContent] => Result): Action[AnyContent] = {
    apply(BodyParsers.parse.anyContent)(requestHandler)
  }

  def apply[A](bodyParser: BodyParser[A])(requestHandler: SecureRequest[A] => Result): Action[A] = {
    Action(bodyParser) { req =>
      findUserBySession(req) match {
        case Some(user) => requestHandler(SecureRequest(user, req))
        case _ => Errors.Unauthorized.toResult
      }
    }
  }

  def async(requestHandler: SecureRequest[AnyContent] => Future[Result]): Action[AnyContent] = {
    async(BodyParsers.parse.anyContent)(requestHandler)
  }

  def async[A](bodyParser: BodyParser[A])(requestHandler: SecureRequest[A] => Future[Result]): Action[A] = {
    Action.async(bodyParser) { req =>
      findUserBySession(req) match {
        case Some(user) => requestHandler(SecureRequest(user, req))
        case _ => Future.successful(Errors.Unauthorized.toResult)
      }
    }
  }
}
