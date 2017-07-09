package jp.iwmat.sawtter.modules

import com.google.inject.AbstractModule
import play.api.{ Configuration, Environment, Logger }

import jp.iwmat.sawtter.infrastructure.cache._shade._
import jp.iwmat.sawtter.repositories._

class ShadeCacheModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def configure() = {
    bind(classOf[Cache]).to(classOf[ShadeCache])
  }
}
