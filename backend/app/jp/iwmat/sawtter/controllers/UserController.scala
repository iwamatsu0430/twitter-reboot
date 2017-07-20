package jp.iwmat.sawtter.controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.{ ExecutionContext, Future }

import jp.iwmat.sawtter.services.SessionService

@Singleton
class UserController @Inject() (
  val sessionService: SessionService
)(
  implicit
  val ec: ExecutionContext
) extends ControllerBase {

  def me = PublicAction { implicit req =>
    Ok
  }
}
