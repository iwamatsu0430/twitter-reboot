package jp.iwmat.sawtter.controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.{ ExecutionContext, Future }

import play.api.libs.json.Json

import jp.iwmat.sawtter.Result
import jp.iwmat.sawtter.controllers.mappers.PageMapper
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._
import jp.iwmat.sawtter.services.{ PageService, SessionService }

@Singleton
class PageController @Inject()(
  pageService: PageService,
  val sessionService: SessionService
)(
  implicit
  val ec: ExecutionContext
) extends ControllerBase with PageMapper {

  def canIFrame(urlValue: String) = Action.async {
    (for {
      url <- Result(URL(urlValue))
      _ <- Result.either(url.isValid) or Errors.InvalidURLParam(urlValue)
      result <- pageService.canIFrame(url)
    } yield result).toResult
  }

  // TODO
  def fetchImage(urlValue: String) = Action.async {
    scala.concurrent.Future.successful(Ok)
  }

  def listComment(urlValue: String) = Action.async {
    (for {
      url <- Result(URL(urlValue))
      _ <- Result.either(url.isValid) or Errors.InvalidURLParam(urlValue)
      comments <- pageService.listComments(url)
    } yield comments).toResult
  }

  def addComment(urlValue: String) = SecureAction.async(parse.json) { implicit req =>
    (for {
      url <- Result(URL(urlValue))
      _ <- Result.either(url.isValid) or Errors.InvalidURLParam(urlValue)
      payload <- deserializeT[NewComment, Future]
      _ <- pageService.addComment(url, payload)
    } yield ()).toResult
  }
}
