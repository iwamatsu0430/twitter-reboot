package jp.iwmat.sawtter.infrastructure.jdbc._slick

import java.time.ZonedDateTime
import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter.generators.{ Clocker, IdentifyBuilder }
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.repositories._

class PageRepositorySlick @Inject()(
  identifyBuilder: IdentifyBuilder,
  clocker: Clocker
)(
  implicit
  ec: ExecutionContext
) extends RepositoryBaseSlick with PageRepository {

  def listComments(url: String): DBResult[Seq[Comment]] = {
    val dbio = sql"""
      select
        comment_id, url, user_id, text, created_at
      from
        comments
      where
        url = $url and
        status != ${CommentStatus.Deleted.value}
    """.as[(Long, String, Long, String, ZonedDateTime)]
      .map(_.map {
        case (commentId, url, userId, text, createdAt) =>
          Comment(commentId, url, userId, text, createdAt)
      })
    DBIOResult(dbio)
  }

  def addComment(url: String, text: String)(implicit ctx: User): DBResult[Unit] = {
    val commentId = identifyBuilder.generate()
    val justNow = clocker.now
    val dbio = sqlu"""
      insert into
        comments
        (comment_id, user_id, url, text, status, updated_at, created_at)
      values
        ($commentId, ${ctx.userId}, $url, $text, ${CommentStatus.Alived.value}, $justNow, $justNow)
    """.map(_ => ())
    DBIOResult(dbio)
  }
}
