package models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.index.IndexResponse
import utils.ElasticsearchUtil
import utils.HashUtil.crypt

/**
 * @author SAW
 */
case class Member(memberId: String, password: String, confirmed: Boolean) {

  // ===================================================================================
  //                                                                               Match
  //                                                                               =====

  /**
   * パスワードが一致するかどうか
   * @param password パスワード（ハッシュ前）
   * @return 比較結果
   */
  def isMatch(password: String): Boolean = this.password == crypt(password)

  // ===================================================================================
  //                                                                             Confirm
  //                                                                             =======

  /**
   * メールアドレス確認済みのメンバーにする
   */
  def confirm: Unit = ElasticsearchUtil.process { client =>
    client.execute(update id memberId in MemberModel.INDEX_TYPE doc "confirmed" -> true)
  }
}

/**
 * @author SAW
 */
object MemberModel {

  val INDEX_TYPE: String = "twitter/member"

  // ===================================================================================
  //                                                                          New member
  //                                                                          ==========

  /**
   * メンバーを新規作成する
   * @param mail メールアドレス
   * @param password パスワード
   * @return 作成されたメンバー
   */
  def create(mail: String, password: String): Future[Member] = ElasticsearchUtil.process { client =>
    val cryptPassword = crypt(password)
    val futureSearching: Future[IndexResponse] = client.execute(index into INDEX_TYPE fields (
      "mail" -> mail,
      "password" -> cryptPassword,
      "confirmed" -> false
    ))
    futureSearching.map(f => Member(f.getId, cryptPassword, false))
  }

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====

  /**
   * メールアドレスからメンバーを探す
   * @param mail メールアドレス
   * @return メンバー
   */
  def findByMail(mail: String): Future[Option[Member]] = findMemberBySingleKey("mail", mail)

  /**
   * メンバーIDからメンバーを探す
   * @param memberId メンバーID
   * @return メンバー
   */
  def findById(memberId: String): Future[Option[Member]] = findMemberBySingleKey("_id", memberId)

  /**
   * メンバー探索の内部メソッド
   * なにか一つの値をもとにメンバーを探す
   * @param key 探索キー
   * @param value 探索値
   * @return メンバー
   */
  def findMemberBySingleKey(key: String, value: String): Future[Option[Member]] = ElasticsearchUtil.process { client =>
    client.execute(search in INDEX_TYPE query {
      matches(key, value)
    }).map(_.getHits.getHits.headOption.map(hit => {
      val source = hit.getSource
      Some(Member(hit.getId, source.get("password").asInstanceOf[String], source.get("confirmed").asInstanceOf[Boolean]))
    }).getOrElse(None))
  }

  /**
   * メンバーIDのリストを元にメンバーのリストを探索
   * @param memberIdList メンバーIDリスト
   * @return メンバーリスト
   */
  def findByIdList(memberIdList: List[String]): Future[List[Member]] = ElasticsearchUtil.process { client =>
    client.execute(search in INDEX_TYPE query {
      filteredQuery filter termsFilter("_id",memberIdList:_*)
    }).map(_.getHits.getHits.toList.map(hit => {
      val source = hit.getSource
      Member(hit.getId, source.get("password").asInstanceOf[String], source.get("confirmed").asInstanceOf[Boolean])
    }))
  }
}
