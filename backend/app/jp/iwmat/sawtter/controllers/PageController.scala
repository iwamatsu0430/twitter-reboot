package jp.iwmat.sawtter.controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.ExecutionContext

import play.api.libs.json.Json

import jp.iwmat.sawtter.models.Comment
import jp.iwmat.sawtter.services.{ PageService, SessionService }

@Singleton
class PageController @Inject()(
  pageService: PageService,
  val sessionService: SessionService
)(
  implicit
  val ec: ExecutionContext
) extends ControllerBase {

  implicit val commentWrites = Json.writes[Comment]

  def canIFrame(url: String) = Action.async {
    pageService.canIFrame(url).toResult
  }

  // TODO
  def fetchImage(url: String) = Action.async {
    scala.concurrent.Future.successful(Ok)
  }

  def listComment(url: String) = Action.async {
    pageService.listComments(url).toResult
  }

  // TODO
  def postComment(url: String) = SecureAction.async { implicit req =>
    scala.concurrent.Future.successful(Ok)
  }
}
