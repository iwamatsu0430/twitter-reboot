package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models.{ Comment, User }

trait PageRepository {
  def listComments(url: String): DBResult[Seq[Comment]]
  def addComment(url: String, text: String)(implicit ctx: User): DBResult[Unit]
}
