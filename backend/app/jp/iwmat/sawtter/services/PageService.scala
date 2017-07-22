package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.http.PageHttp
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types.URL
import jp.iwmat.sawtter.repositories._

class PageService @Inject()(
  pageHttp: PageHttp,
  pageRepository: PageRepository
)(
  implicit
  ec: ExecutionContext,
  rdb: RDB
) {

  def canIFrame(url: URL[_]): Result[Boolean] = {
    pageHttp.canIFrame(url)
  }

  def listComments(url: URL[_]): Result[Seq[Comment]] = {
    val result = pageRepository.listComments(url)
    rdb.exec(result)
  }

  def addComment(url: URL[_], comment: NewComment)(implicit ctx: User): Result[Unit] = {
    val result = pageRepository.addComment(url, comment)
    rdb.exec(result)
  }
}
