package jp.iwmat.sawtter.controllers.secure

import play.api.mvc.{ Request, WrappedRequest }

import jp.iwmat.sawtter.models.User

case class SecureRequest[A](
  user: User,
  request: Request[A]
) extends WrappedRequest[A](request)
