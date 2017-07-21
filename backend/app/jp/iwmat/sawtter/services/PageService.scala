package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.http.PageHttp
import jp.iwmat.sawtter.models.Comment
import jp.iwmat.sawtter.repositories.PageRepository

class PageService @Inject()(
  pageHttp: PageHttp,
  pageRepository: PageRepository
) {

  def canIFrame(url: String): Result[Boolean] = {
    pageHttp.canIFrame(url)
  }

  def listComments(url: String): Result[Seq[Comment]] = {
    ???
  }
}
