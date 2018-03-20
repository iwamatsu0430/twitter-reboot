package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models._

trait SessionRepository {
  def fetch(key: String): Option[User]
  def add(user: User): String
  def delete(user: User): Unit
}
