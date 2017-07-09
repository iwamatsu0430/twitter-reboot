package jp.iwmat.sawtter.repositories

import scala.concurrent.duration.Duration

import play.api.libs.json._

trait Cache {
  def set[A](key: String, value: A, timeout: Duration): Unit
  def setJson[A](key: String, value: A, timeout: Duration)(implicit writes: Writes[A]): Unit
  def getString(key: String): Option[String]
  def getInt(key: String): Option[Int]
  def getBoolean(key: String): Option[Boolean]
  def getJson[A](key: String)(implicit reads: Reads[A]): Option[A]
}
