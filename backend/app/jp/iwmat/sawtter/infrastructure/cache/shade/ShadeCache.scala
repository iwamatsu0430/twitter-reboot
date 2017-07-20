package jp.iwmat.sawtter.infrastructure.cache._shade

import javax.inject.{ Inject, Named, Singleton }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

import play.api.libs.json._
import shade.memcached._

import jp.iwmat.sawtter.repositories.Cache

@Singleton
class ShadeCache @Inject()(
  @Named("memcached.host") host: String,
  @Named("memcached.port") port: Int
)(
  implicit
  ec: ExecutionContext
) extends Cache {

  val client = Memcached(Configuration(s"$host:$port"))

  def set[A](key: String, value: A, timeout: Duration): Unit =
    client.awaitSet(key, value.toString, timeout)

  def setJson[A](key: String, value: A, timeout: Duration)(implicit writes: Writes[A]): Unit =
    client.awaitSet(key, Json.toJson(value).toString, timeout)

  def getString(key: String): Option[String] =
    client.awaitGet[String](key)

  def getInt(key: String): Option[Int] =
    client.awaitGet[Int](key)

  def getBoolean(key: String): Option[Boolean] =
    client.awaitGet[Boolean](key)

  def getJson[A](key: String)(implicit reads: Reads[A]): Option[A] =
    getString(key)
      .flatMap(Json.parse(_).asOpt[A])

  def delete(key: String): Unit =
    client.delete(key)
}
