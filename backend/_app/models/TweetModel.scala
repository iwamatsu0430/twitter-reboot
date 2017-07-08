package models

import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, LocalDateTime}
import java.util

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.sort.SortOrder
import play.api.libs.json._

import utils.ElasticsearchUtil

/**
 * @author SAW
 */
case class TweetJson(tweetId: String, shareContentsSurfaceUrl: String, comment: String, postedAt: String, timestamp: Long, replyToTweetId: Option[String])

/**
 * @author SAW
 */
case class Tweet(tweetId: String, memberId: String, shareContentsSurfaceUrl: String, shareContentsId: String, comment: String, timestamp: Long, replyToTweetId: Option[String], deleted: Boolean) {

  // ===================================================================================
  //                                                                          Attributes
  //                                                                          ==========

  /**
   * 投稿日
   */
  val postedAt: String = {
    val postedDateTime: LocalDateTime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.ofHours(9))
    postedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
  }

  // ===================================================================================
  //                                                                   Compare timestamp
  //                                                                   =================

  /**
   * 指定の日付以前のツイートであるか比較する
   * @param targetTimestamp 指定日付
   * @return 比較結果
   */
  def isBefore(targetTimestamp: Long): Boolean = timestamp <= targetTimestamp

  /**
   * 指定の日付以後のツイートであるか比較する
   * @param targetTimestamp 指定日付
   * @return 比較結果
   */
  def isAfter(targetTimestamp: Long): Boolean = timestamp > targetTimestamp

  // ===================================================================================
  //                                                                             Convert
  //                                                                             =======

  /**
   * Json出力
   * @return Json
   */
  def toJson: JsValue = Json.toJson(toTweetJson)(Json.writes[TweetJson])

  /**
   * Json出力用のクラスへ変換する
   * @return
   */
  def toTweetJson: TweetJson = TweetJson(tweetId, shareContentsSurfaceUrl, comment, postedAt, timestamp, replyToTweetId)

}

/**
 * @author SAW
 */
object TweetModel {

  val INDEX_TYPE: String = "twitter/tweet"

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====

  /**
   * ツイートIDでツイートを探索する
   * @param targetTweetId ツイートID
   * @return ツイート
   */
  def findById(targetTweetId: String): Future[Option[Tweet]] = ElasticsearchUtil.process { client =>
    client.execute(search in INDEX_TYPE fields "_timestamp" fields "_source" query {
      matches ("_id", targetTweetId)
    }).map { result =>
      result.getHits.getHits.headOption.map(mapping)
    }
  }

  /**
   * シェアコンテンツIDでツイートを探索する
   * @param shareContentsId シェアコンテンツID
   * @return ツイートリスト
   */
  def findByShareContentsIds(shareContentsId: String): Future[List[Tweet]] = ElasticsearchUtil.process { client =>
    client.execute(search in INDEX_TYPE fields "_timestamp" fields "_source" size 10 query {
      filteredQuery filter {
        andFilter(
          termFilter("shareContentsId", shareContentsId),
          termFilter("deleted", false)
        )
      }
    } sort (
      by field "_timestamp" order SortOrder.DESC
    )).map { result =>
      result.getHits.getHits.toList.map(mapping)
    }
  }

  // ===================================================================================
  //                                                                                  Do
  //                                                                                  ==

  /**
   * ツイートする
   * @param authorMemberId 書き込みしたメンバーID
   * @param surfaceUrl 見かけのURL（未使用）
   * @param comment コメント
   * @param shareContents シェアコンテンツ
   * @return ツイートID
   */
  def tweet(authorMemberId: String, surfaceUrl: String, comment: String, shareContents: ShareContents): Future[String] = ElasticsearchUtil.process { client =>
    client.execute(index into INDEX_TYPE fields (
      "memberId" -> authorMemberId,
      "comment" -> comment,
      "shareContentsSurfaceUrl" -> surfaceUrl,
      "shareContentsId" -> shareContents.shareContentsId,
      "deleted" -> false
    )).map(_.getId)
  }

  /**
   * ツイートを削除する
   * @param tweet 削除対象のツイート
   */
  def delete(tweet: Tweet): Unit = ElasticsearchUtil.process { client =>
    client.execute(update id tweet.tweetId in INDEX_TYPE doc "deleted" -> true)
  }

  // ===================================================================================
  //                                                                              Helper
  //                                                                              ======

  /**
   * 検索結果をツイートクラスへマッピングする
   * @param hit 検索結果
   * @return ツイート
   */
  def mapping(hit: SearchHit): Tweet = {
    val source: util.Map[String, AnyRef] = hit.getSource
    val memberId: String = source.get("memberId").asInstanceOf[String]
    val shareContentsUrl: String = source.get("shareContentsSurfaceUrl").asInstanceOf[String]
    val shareContentsId: String = source.get("shareContentsId").asInstanceOf[String]
    val comment: String = source.get("comment").asInstanceOf[String]
    val timestamp = hit.field("_timestamp").getValue.toString.toLong
    val replyToTweetId = source.get("replyToTweetId").asInstanceOf[String] match {
      case null => None
      case r => Some(r)
    }
    val deleted = source.get("deleted").asInstanceOf[Boolean]

    Tweet(hit.getId, memberId, shareContentsUrl, shareContentsId, comment, timestamp, replyToTweetId, deleted)
  }
}
