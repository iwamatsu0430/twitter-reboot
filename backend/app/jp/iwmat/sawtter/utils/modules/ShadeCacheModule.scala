package jp.iwmat.sawtter.utils.modules

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import play.api.{ Configuration, Environment, Logger }

import jp.iwmat.sawtter.infrastructure.cache._shade._
import jp.iwmat.sawtter.repositories._

class ShadeCacheModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def keyException(key: String) = throw new Exception(s"configure key not found. key=$key")

  val hostKey = "memcached.host"

  val portKey = "memcached.port"

  def configure() = {
    bind(classOf[String])
      .annotatedWith(Names.named(hostKey))
      .toInstance(
        configuration
          .getString(hostKey)
          .getOrElse(keyException(hostKey))
      )
    bind(classOf[Int])
      .annotatedWith(Names.named(portKey))
      .toInstance(
        configuration
          .getInt(portKey)
          .getOrElse(keyException(portKey))
      )
    bind(classOf[SessionRepository]).to(classOf[SessionRepositoryShade])
  }
}
