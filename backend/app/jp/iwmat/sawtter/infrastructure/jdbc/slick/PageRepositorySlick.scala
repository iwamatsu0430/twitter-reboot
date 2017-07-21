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
      c.comment_id, c.url, c.user_id, c.text, count(cf.comment_favorites_id) as favorites  , c.created_at
    from
      comments as c
      left join comment_favorites as cf
        on cf.comment_id = c.comment_id
    where
      c.url = $url and
      c.status != ${CommentStatus.Deleted.value}
    group by
      c.comment_id
    order by
      c.created_at desc
    """.as[(Long, String, Long, String, Long, ZonedDateTime)]
      .map(_.map {
        case (commentId, url, userId, text, favorites, createdAt) =>
          Comment(commentId, url, userId, text, favorites, createdAt)
      })
    DBIOResult(dbio)
  }

  def addComment(url: String, comment: NewComment)(implicit ctx: User): DBResult[Unit] = {
    val commentId = identifyBuilder.generate()
    val justNow = clocker.now
    val dbio = sqlu"""
      insert into
        comments
        (comment_id, user_id, url, text, status, updated_at, created_at)
      values
        ($commentId, ${ctx.userId}, $url, ${comment.text}, ${CommentStatus.Alived.value}, $justNow, $justNow)
    """.map(_ => ())
    DBIOResult(dbio)
  }
}
