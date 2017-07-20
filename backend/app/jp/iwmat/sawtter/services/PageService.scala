package jp.iwmat.sawtter.services

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.external.PageExternal
import jp.iwmat.sawtter.models.Comment

class PageService @Inject()(
  pageExternal: PageExternal
) {

  def confirm(url: String): Result[Boolean] = {
    pageExternal.confirm(url)
  }

  def listComments(url: String): Result[Seq[Comment]] = {
    ???
  }
}
