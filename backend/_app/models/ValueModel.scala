package models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.{Json, JsValue}
import com.sksamuel.elastic4s.ElasticDsl._

import utils.ElasticsearchUtil

case class ValueCount(good: String, bad: String, isValued: Boolean = false) {

  def toJson: JsValue = Json.toJson(this)(Json.writes[ValueCount])
}

/**
 * @author SAW
 */
object ValueModel {

  val INDEX_TYPE: String = "twitter/tweetValue"

  /**
   * Good評価をつける
   * @param member 評価者
   * @param tweet 対象ツイート
   */
  def good(member: Member, tweet: Tweet): Unit = putValue(member, tweet, 1)

  /**
   * Bad評価をつける
   * @param member 評価者
   * @param tweet 対象ツイート
   */
  def bad(member: Member, tweet: Tweet): Unit = putValue(member, tweet, -1)

  /**
   * 評価をつける
   * @param member 評価者
   * @param tweet 対象ツイート
   * @param valueScore 評価スコア
   */
  def putValue(member: Member, tweet: Tweet, valueScore: Integer): Unit = ElasticsearchUtil.process { client =>
    client.execute(index into INDEX_TYPE fields (
      "valueFromMemberId" -> member.memberId,
      "valueToMemberId" -> tweet.memberId,
      "valueToTweetId" -> tweet.tweetId,
      "valueScore" -> valueScore
    ))
  }

  /**
   * 評価を取り消す
   * @param member 評価者
   * @param tweet 対象ツイート
   */
  def cancel(member: Member, tweet: Tweet): Unit = ElasticsearchUtil.process { client =>
    client.execute(search in INDEX_TYPE query {
      filteredQuery filter {
        andFilter(
          termFilter("valueFromMemberId", member.memberId),
          termFilter("valueToMemberId", tweet.memberId),
          termFilter("valueToTweetId", tweet.tweetId)
        )
      }
    }).foreach { result =>
      result.getHits.getHits.headOption match {
        case None =>
        case Some(hit) => client.execute(delete id hit.getId from INDEX_TYPE)
      }
    }
  }

  /**
   * すでに評価しているかどうか
   * @param member 評価者
   * @param tweet 対象ツイート
   * @return 評価状況
   */
  def existsValued(member: Member, tweet: Tweet): Future[Boolean] = ElasticsearchUtil.process { client =>
    client.execute(search in INDEX_TYPE query {
      filteredQuery filter {
        andFilter(
          termFilter("valueFromMemberId", member.memberId),
          termFilter("valueToTweetId", tweet.tweetId)
        )
      }
    }).map(_.getHits.getHits.size > 0)
  }

  /**
   * ツイートの評価を取得する(すでに評価しているかどうかも取得する)
   * @param tweet 対象ツイート
   * @param loginMemberOpt ログインしているメンバー
   * @return ツイート評価
   */
  def countValueByTweet(tweet: Tweet, loginMemberOpt: Option[Member]): Future[ValueCount] = ElasticsearchUtil.process { client =>
    client.execute(count from INDEX_TYPE query {
      matchQuery("valueToTweetId", tweet.tweetId)
    }).flatMap { t =>
      val total = t.getCount
      client.execute(count from INDEX_TYPE query {
        filteredQuery filter {
          andFilter(
            termFilter("valueToTweetId", tweet.tweetId),
            termFilter("valueScore", 1)
          )
        }
      }).flatMap { hit =>
        val good = hit.getCount
        loginMemberOpt match {
          case None => Future.successful(ValueCount(good.toString, (total - good).toString, false))
          case Some(loginMember) => existsValued(loginMember, tweet).map { isValued =>
            ValueCount(good.toString, (total - good).toString, isValued)
          }
        }
      }
    }
  }
}
