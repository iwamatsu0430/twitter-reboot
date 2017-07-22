package jp.iwmat.sawtter.controllers.secure

import play.api.mvc.{ Request, WrappedRequest }

import jp.iwmat.sawtter.models.User

case class PublicRequest[A](
  user: Option[User],
  request: Request[A]
) extends WrappedRequest[A](request)
