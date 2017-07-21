package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.http.PageHttp
import jp.iwmat.sawtter.models.Comment

class PageService @Inject()(
  pageHttp: PageHttp
) {

  def confirm(url: String): Result[Boolean] = {
    pageHttp.confirm(url)
  }

  def listComments(url: String): Result[Seq[Comment]] = {
    ???
  }
}
