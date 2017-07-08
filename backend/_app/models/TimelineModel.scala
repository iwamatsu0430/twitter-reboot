package models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import play.api.libs.json._

import utils.ElasticsearchUtil

case class TimelineJson(tweet: TweetJson, shareContents: ShareContents, value: ValueCount, isMine: Option[Boolean] = None)

case class TimelineObject(tweet: Tweet, shareContents: ShareContents, valueCount: ValueCount, loginMemberOpt: Option[Member] = None) {

  /**
   * Jsonで出力する
   */
  def toJson: JsValue = {
    implicit val writesTweetJson = Json.writes[TweetJson]
    implicit val writesShareContents = Json.writes[ShareContents]
    implicit val writesValueCount = Json.writes[ValueCount]
    loginMemberOpt match {
      case None => Json.toJson(TimelineJson(tweet.toTweetJson, shareContents, valueCount))(Json.writes[TimelineJson])
      case Some(loginMember) => {
        val isMine = tweet.memberId == loginMember.memberId
        Json.toJson(TimelineJson(tweet.toTweetJson, shareContents, valueCount, Some(isMine)))(Json.writes[TimelineJson])
      }
    }
  }
}

/**
 * @author SAW
 */
object TimelineModel {

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====

  /**
   * ツイートを最大20件取得する
   * 条件により、フィルタリングをかける
   * @param loginMemberOpt ログインメンバー(ログインしていない場合もある)
   * @param before この時間より前のツイートにフィルタリング
   * @param after この時間より後のツイートにフィルタリング
   */
  def findAll(loginMemberOpt: Option[Member], before: Long, after: Long): Future[List[TimelineObject]] = ElasticsearchUtil.process { client =>
    client.execute(search in TweetModel.INDEX_TYPE fields "_timestamp" fields "_source" size 20 query {
      filteredQuery filter {
        andFilter(
          termFilter("deleted", false),
          numericRangeFilter("_timestamp") lte before gt after
        )
      }
    } sort (
      by field "_timestamp" order SortOrder.DESC
    )).map(_.getHits.getHits.toList.map { hit =>
      val tweet = TweetModel.mapping(hit)
      for (
        valueCount <- ValueModel.countValueByTweet(tweet, loginMemberOpt);
        shareContents <- ShareContentsModel.findById(hit.getSource.get("shareContentsId").asInstanceOf[String])
      ) yield TimelineObject(tweet, shareContents, valueCount, loginMemberOpt)
    }).flatMap (Future.sequence(_))
  }
}
