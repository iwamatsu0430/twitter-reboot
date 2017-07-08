package models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex

import com.sksamuel.elastic4s.ElasticDsl._
import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.WS
import play.api.Play.current

import utils.ElasticsearchUtil
import utils.HashUtil.crypt

case class ShareContentsDetail(shareContents: ShareContents, tweets: List[Tweet], loginMemberOpt: Option[Member]) {

  // TODO SAW 無駄っぽい処理（自力でJson化など）があるので要修正

  /**
    * Json出力
   * @return Json
   */
  def toJson: Future[JsValue] = {
    val inner = tweets.map { tweet =>
      ValueModel.countValueByTweet(tweet, loginMemberOpt).map { valueCount =>
        Map(
          "tweet" -> tweet.toJson,
          "value" -> valueCount.toJson,
          "identityHash" -> Json.toJson(crypt(tweet.memberId + " - SAW APP - " + shareContents.shareContentsId)),
          "isMine" -> Json.toJson(loginMemberOpt.map(_.memberId == tweet.memberId).getOrElse(false))
        )
      }
    }
    Future.traverse(inner) { m =>
      m.map(Json.toJson(_))
    }.map { list =>
      Json.toJson(Map(
        "shareContents" -> shareContents.toJson,
        "tweets" -> Json.toJson(list)
      ))
    }
  }
}

case class ShareContents(shareContentsId: String, url: String, title: String, thumbnailUrl: String) {

  /**
   * Json出力
   * @return Json
   */
  def toJson: JsValue = Json.toJson(this)(Json.writes[ShareContents])
}

/**
 * @author SAW
 */
object ShareContentsModel {

  val INDEX_TYPE: String = "twitter/shareContents"

  /**
   * シェアコンテンツをDB上に作成する
   * 同一コンテンツが既に存在しない事が前提
   * @param url コンテンツURL
   * @return 作成したコンテンツ
   */
  private def create(url: String): Future[ShareContents] = ElasticsearchUtil.process { client =>
    val thumbRequest = WS.url(s"http://capture.heartrails.com/small?$url") // get thumbnail
    fetchHtmlTitle(url).flatMap { title =>
      thumbRequest.get.flatMap { thumbResponse =>
        val thumbnailUrl = thumbRequest.url
        client.execute(index into INDEX_TYPE fields (
          "url" -> url,
          "thumbnailUrl" -> thumbnailUrl,
          "title" -> title
          )).map { result =>
          ShareContents(result.getId, url, title, thumbnailUrl)
        }
      }
    }
  }

  /**
   * URLでDB上のシェアコンテンツを探す
   * 存在しない場合は新たに作成する
   * @param url コンテンツURL
   * @return 探索（作成）されたコンテンツ
   */
  def createOrFind(url: String): Future[ShareContents] = ElasticsearchUtil.process { client =>
    val request = WS.url(s"http://api.hitonobetsu.com/surl/open?url=$url") // get original url
    request.get.flatMap { response =>
    val json: JsValue = Json.parse(response.body)
      val originalUrl: String = (json \ "original").get.as[String]
      client.execute(search in INDEX_TYPE query {
        matches("url", originalUrl)
      }).flatMap(_.getHits.getHits.headOption.map { head =>
        // when exists DB
        val source = head.getSource
        Future.successful(ShareContents(head.getId, originalUrl, source.get("title").asInstanceOf[String], source.get("thumbnailUrl").asInstanceOf[String]))
      }.getOrElse({
        // when not exists DB
        create(originalUrl)
      }))
    }
  }

  /**
   * シェアコンテンツをIDから検索する
   * @param shareContentsId シェアコンテンツID
   * @return
   */
  def findById(shareContentsId: String): Future[ShareContents] = ElasticsearchUtil.process { client =>
    client.execute(get id shareContentsId from INDEX_TYPE).map { hit =>
      val source = hit.getSource
      ShareContents(hit.getId, source.get("url").asInstanceOf[String], source.get("title").asInstanceOf[String], source.get("thumbnailUrl").asInstanceOf[String])
    }
  }

  // ===================================================================================
  //                                                                              Helper
  //                                                                              ======

  /**
   * URLからHTMLタイトルを取得する
   * @param url 対象URL
   * @return タイトル
   */
  def fetchHtmlTitle(url: String): Future[String] = WS.url(url).get.map { response =>
    val pattern: Regex = """<title>(.*)<\/title>""".r
    pattern.findFirstIn(response.body).map {
      case pattern(title) => title
      case _ => "No Title"
    }.get
  }
}
