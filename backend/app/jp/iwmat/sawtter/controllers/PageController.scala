package jp.iwmat.sawtter.controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.{ ExecutionContext, Future }

import play.api.libs.json.Json

import jp.iwmat.sawtter.models._
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
  implicit val newCommentReads = Json.reads[NewComment]

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

  def addComment(url: String) = SecureAction.async(parse.json) { implicit req =>
    (for {
      payload <- deserializeT[NewComment, Future]
      // TODO validation length 1 ~ 140
      _ <- pageService.addComment(url, payload)
    } yield ()).toResult
  }
}
