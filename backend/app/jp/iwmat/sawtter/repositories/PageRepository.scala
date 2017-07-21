package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models._

trait PageRepository {
  def listComments(url: String): DBResult[Seq[Comment]]
  def addComment(url: String, comment: NewComment)(implicit ctx: User): DBResult[Unit]
}
