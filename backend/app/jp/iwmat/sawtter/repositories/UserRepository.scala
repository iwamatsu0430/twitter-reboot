package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models.{ SignUp, User }

trait UserRepository {
  def findBy(email: String): DBResult[Option[User]]
  def add(signup: SignUp): DBResult[Long]
  def addToken(userId: Long): DBResult[String]
}
