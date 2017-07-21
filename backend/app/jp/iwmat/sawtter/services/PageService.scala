package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.http.PageHttp
import jp.iwmat.sawtter.models.Comment
import jp.iwmat.sawtter.repositories._

class PageService @Inject()(
  pageHttp: PageHttp,
  pageRepository: PageRepository
)(
  implicit
  ec: ExecutionContext,
  rdb: RDB
) {

  def canIFrame(url: String): Result[Boolean] = {
    pageHttp.canIFrame(url)
  }

  def listComments(url: String): Result[Seq[Comment]] = {
    val result = pageRepository.listComments(url)
    rdb.exec(result)
  }
}
