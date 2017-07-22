package jp.iwmat.sawtter.controllers.secure

import scala.concurrent.Future

import play.api.mvc._

trait PublicAction extends ActionBase {

  def apply(requestHandler: PublicRequest[AnyContent] => Result): Action[AnyContent] = {
    apply(BodyParsers.parse.anyContent)(requestHandler)
  }

  def apply[A](bodyParser: BodyParser[A])(requestHandler: PublicRequest[A] => Result): Action[A] = {
    Action(bodyParser) { req =>
      requestHandler(PublicRequest(findUserBySession(req), req))
    }
  }

  def async(requestHandler: PublicRequest[AnyContent] => Future[Result]): Action[AnyContent] = {
    async(BodyParsers.parse.anyContent)(requestHandler)
  }

  def async[A](bodyParser: BodyParser[A])(requestHandler: PublicRequest[A] => Future[Result]): Action[A] = {
    Action.async(bodyParser) { req =>
      requestHandler(PublicRequest(findUserBySession(req), req))
    }
  }
}
