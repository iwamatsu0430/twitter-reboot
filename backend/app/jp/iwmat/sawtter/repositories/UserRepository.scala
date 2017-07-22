package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._

trait UserRepository {
  def findBy(userId: Long): DBResult[Option[User]]
  def findBy(email: Email[SignUp]): DBResult[Option[User]]
  def findBy(login: Login): DBResult[Option[User]]
  def findToken(token: String): DBResult[Option[UserToken]]
  def add(signup: SignUp): DBResult[UserToken]
  def enable(user: User): DBResult[Unit]
}
