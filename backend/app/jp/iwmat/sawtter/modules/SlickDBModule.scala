package jp.iwmat.sawtter.modules

import com.google.inject.AbstractModule
import play.api.{ Configuration, Environment, Logger }

import jp.iwmat.sawtter.infrastructure.jdbc._slick._
import jp.iwmat.sawtter.repositories._

class SlickDBModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def configure() = {
    bind(classOf[RDB]).to(classOf[SlickDB])
    bind(classOf[UserRepository]).to(classOf[UserRepositorySlick])
    bind(classOf[PageRepository]).to(classOf[PageRepositorySlick])
  }
}
