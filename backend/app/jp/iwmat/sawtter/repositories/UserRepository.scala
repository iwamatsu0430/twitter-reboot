package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models.{ Login, SignUp, User, UserToken }

trait UserRepository {
  def findBy(userId: Long): DBResult[Option[User]]
  def findBy(email: String): DBResult[Option[User]]
  def findBy(login: Login): DBResult[Option[User]]
  def findToken(token: String): DBResult[Option[UserToken]]
  def add(signup: SignUp): DBResult[UserToken]
  def enable(user: User): DBResult[Unit]
}
