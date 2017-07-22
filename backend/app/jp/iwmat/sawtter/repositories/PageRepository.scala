package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types.URL

trait PageRepository {
  def listComments(url: URL[_]): DBResult[Seq[Comment]]
  def addComment(url: URL[_], comment: NewComment)(implicit ctx: User): DBResult[Unit]
}
