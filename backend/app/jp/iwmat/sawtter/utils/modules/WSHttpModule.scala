package jp.iwmat.sawtter.utils.modules

import com.google.inject.AbstractModule
import play.api.{ Configuration, Environment, Logger }

import jp.iwmat.sawtter.infrastructure.http._ws._
import jp.iwmat.sawtter.utils.http._

class WSHttpModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def configure() = {
    bind(classOf[PageHttp]).to(classOf[PageHttpWS])
  }
}
