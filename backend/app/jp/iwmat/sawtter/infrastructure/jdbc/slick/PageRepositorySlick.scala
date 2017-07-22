package jp.iwmat.sawtter.infrastructure.jdbc._slick

import java.time.ZonedDateTime
import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter.generators.{ Clocker, IdentifyBuilder }
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._
import jp.iwmat.sawtter.repositories._

class PageRepositorySlick @Inject()(
  identifyBuilder: IdentifyBuilder,
  clocker: Clocker
)(
  implicit
  ec: ExecutionContext
) extends RepositoryBaseSlick with PageRepository {

  def listComments(url: URL[_]): DBResult[Seq[Comment]] = {
    // FIXME typeのgetをつくる
    val dbio = sql"""
    select
      c.comment_id, c.url, c.user_id, c.text, count(cf.comment_favorites_id) as favorites  , c.created_at
    from
      comments as c
      left join comment_favorites as cf
        on cf.comment_id = c.comment_id
    where
      c.url = ${url.value} and
      c.status != ${CommentStatus.Deleted.value}
    group by
      c.comment_id
    order by
      c.created_at desc
    """.as[(Long, String, Long, String, Long, ZonedDateTime)]
      .map(_.map {
        case (commentId, url, userId, text, favorites, createdAt) =>
          Comment(ID(commentId), URL(url), ID(userId), CommentText(text), favorites, createdAt)
      })
    DBIOResult(dbio)
  }

  def addComment(url: URL[_], comment: NewComment)(implicit ctx: User): DBResult[Unit] = {
    val commentId = identifyBuilder.generate()
    val justNow = clocker.now
    // FIXME typeのsetをつくる
    val dbio = sqlu"""
      insert into
        comments
        (comment_id, user_id, url, text, status, updated_at, created_at)
      values
        ($commentId, ${ctx.userId.value}, ${url.value}, ${comment.text.value}, ${CommentStatus.Alived.value}, $justNow, $justNow)
    """.map(_ => ())
    DBIOResult(dbio)
  }
}
