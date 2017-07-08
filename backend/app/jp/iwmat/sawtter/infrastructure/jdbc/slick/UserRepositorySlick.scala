package jp.iwmat.sawtter.infrastructure.jdbc._slick

import java.time.ZonedDateTime
import javax.inject.Inject

import scala.concurrent.ExecutionContext

import jp.iwmat.sawtter.generators.{ Clocker, IdentifyBuilder }
import jp.iwmat.sawtter.models.{ SignUp, User, UserStatus }
import jp.iwmat.sawtter.repositories._

class UserRepositorySlick @Inject()(
  identifyBuilder: IdentifyBuilder,
  clocker: Clocker
)(
  implicit
  ec: ExecutionContext
) extends RepositoryBaseSlick with UserRepository {

  def findAny[A](key: String, value: A)(implicit setter: slick.jdbc.SetParameter[A]): DBResult[Option[User]] = {
    val dbio = sql"""
      select
        user_id, email, status, version, updated_at, created_at
      from
        users
      where
        #$key = $value
      limit 1
    """
      .as[(Long, String, String, Long, ZonedDateTime, ZonedDateTime)]
      .map(_.headOption.flatMap { case (userId, email, status, version, updatedAt, createdAt) =>
        UserStatus.valueOf(status).toOption.map { status =>
          User(userId, email, status, version, updatedAt, createdAt)
        }
      })
    DBIOResult(dbio)
  }

  def findBy(userId: Long): DBResult[Option[User]] = {
    findAny("user_id", userId)
  }

  def findBy(email: String): DBResult[Option[User]] = {
    findAny("email", email)
  }

  def add(signup: SignUp): DBResult[Long] = {
    val userId = identifyBuilder.generate()
    val hashed = identifyBuilder.hash(signup.password)
    val justNow = clocker.now
    val dbio = sql"""
      insert into
        users
        (user_id, email, password, status, version, updated_at, created_at)
      values
        ($userId, ${signup.email}, $hashed, ${UserStatus.Registered.value}, 1, $justNow, $justNow)
    """.as[Int].map(_ => userId)
    DBIOResult(dbio)
  }

  def addToken(userId: Long): DBResult[String] = {
    val justNow = clocker.now
    val token = identifyBuilder.generateUUID()
    val dbio = sql"""
      insert into
        user_tokens
        (token, user_id, expired_at, created_at)
      values
        ($token, $userId, ${justNow.plusDays(7)}, $justNow)
    """.as[Int].map(_ => token)
    DBIOResult(dbio)
  }
}
