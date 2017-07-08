package models

import java.time.LocalDateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl._

import utils.ElasticsearchUtil
import utils.HashUtil.crypt

/**
 * @author SAW
 */
case class MemberConfirmHash(hashId: String, memberId: String, hashValue: String) {

  // ===================================================================================
  //                                                                               Match
  //                                                                               =====

  /**
   * ハッシュ値が一致するか
   * @param hash ハッシュ値
   * @return 比較結果
   */
  def isMatch(hash: String) = hashValue == hash

  // ===================================================================================
  //                                                                             Confirm
  //                                                                             =======

  /**
   * ハッシュを使用済にする
   */
  def complete: Unit = ElasticsearchUtil.process { client =>
    client.execute(update id hashId in MemberConfirmHashModel.INDEX_TYPE doc "used" -> true)
  }
}

/**
 * @author SAW
 */
object MemberConfirmHashModel {

  val INDEX_TYPE: String = "twitter/memberConfirmHash"

  // ===================================================================================
  //                                                                              Create
  //                                                                              ======

  /**
   * メンバー確認ハッシュを作成する
   * @param member メンバー
   * @return ハッシュ値
   */
  def create(member: Member): String = ElasticsearchUtil.process { client =>
    val input = LocalDateTime.now.toString + member.password + member.memberId
    val hash = crypt(input)
    client.execute(index into INDEX_TYPE fields (
      "memberId" -> member.memberId,
      "confirmHash" -> hash,
      "used" -> false
    ))
    hash
  }

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====

  /**
   * メンバーIDからメンバー確認ハッシュを探索する
   * @param memberId メンバーID
   * @return メンバー確認ハッシュ
   */
  def findHashValueByMemberId(memberId: String): Future[Option[MemberConfirmHash]] = ElasticsearchUtil.process { client =>
    client.execute(search in INDEX_TYPE query {
      filteredQuery filter {
        andFilter(
          termFilter("memberId", memberId),
          termFilter("used", false)
        )
      }
    }).map { result =>
      result.getHits.getHits.headOption.map { hit =>
        val source = hit.getSource
        MemberConfirmHash(hit.id, source.get("memberId").asInstanceOf[String], source.get("confirmHash").asInstanceOf[String])
      }
    }
  }
}
