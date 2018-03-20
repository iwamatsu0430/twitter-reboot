package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._

trait UserRepository {
  def findBy(userId: ID[User]): Transaction[Option[User]]
  def findBy(email: Email[SignUp]): Transaction[Option[User]]
  def findBy(login: Login): Transaction[Option[User]]
  def findToken(token: String): Transaction[Option[UserToken]]
  def add(signup: SignUp): Transaction[UserToken]
  def enable(user: User): Transaction[Unit]
}
