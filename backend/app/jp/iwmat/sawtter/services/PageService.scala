package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types.URL
import jp.iwmat.sawtter.repositories._
import jp.iwmat.sawtter.utils.http.PageHttp

class PageService @Inject()(
  pageHttp: PageHttp,
  pageRepo: PageRepository
)(
  implicit
  ec: ExecutionContext,
  rdb: RDB
) extends ServiceBase {

  def canIFrame(url: URL[_]): Result[Boolean] = {
    pageHttp.canIFrame(url)
  }

  def listComments(url: URL[_]): Result[Seq[Comment]] = {
    pageRepo.listComments(url).execute()
  }

  def addComment(url: URL[_], comment: NewComment)(implicit ctx: User): Result[Unit] = {
    pageRepo.addComment(url, comment).execute()
  }
}
