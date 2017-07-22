package jp.iwmat.sawtter.controllers.secure

import play.api.mvc.Request

import jp.iwmat.sawtter.models.User
import jp.iwmat.sawtter.services.SessionService

trait ActionBase {

  def sessionService: SessionService

  def findUserBySession(req: Request[_]): Option[User] = {
    for {
      key <- req.session.get("session")
      user <- sessionService.findBy(key)
    } yield user
  }
}
