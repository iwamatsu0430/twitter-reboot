package actions

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._

import controllers.ResponseCode._
import models.{Member, MemberModel}
import utils.JsonUtil._

/**
 * @author SAW
 */
object AuthAction extends ActionBuilder[Request] {

  /**
   * ログインしている会員情報を取得
   * AuthActionを通ったときのみ使用可能
   */
  def getSessionMember[A](request: Request[A]): Future[Member] = {
    val exception: Exception = new Exception("getSessionMember must be called after AuthAction")
    val loginMemberFuture: Future[Option[Member]] = request.session.get("memberId").map (MemberModel.findById(_)).getOrElse(throw exception)
    loginMemberFuture.map { loginMember =>
      loginMember.getOrElse(throw exception)
    }
  }

  /**
   * ログインしている会員情報を取得
   * ログインしていない場合に呼び出してもよい
   */
  def getSessionMemberOpt[A](request: Request[A]): Future[Option[Member]] = request.session.get("memberId").map { memberId =>
    MemberModel.findById(memberId).map { loginMember =>
      loginMember
    }
  }.getOrElse(Future.successful(None))

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    request.session.get("memberId") match {
      case Some(memberId) => block.apply(request)
      case _ => Future.successful(Results.Status(401).apply(createJson(NeedSignIn)))
    }
  }
}

case class AuthRequest[A](request: Request[A]) extends Request[A] {

  val loginMember = request.session.get("memberId").map { memberId =>
    MemberModel.findById(memberId).map(_.getOrElse(throw new IllegalStateException("This is Illegal Session")))
  }.getOrElse(throw new IllegalStateException("AuthRequest must have memberId"))

  override def body: A = request.body

  override def secure: Boolean = request.secure

  override def uri: String = request.uri

  override def remoteAddress: String = request.remoteAddress

  override def queryString: Map[String, Seq[String]] = request.queryString

  override def method: String = request.method

  override def headers: Headers = request.headers

  override def path: String = request.path

  override def version: String = request.version

  override def tags: Map[String, String] = request.tags

  override def id: Long = request.id
}