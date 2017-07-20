package jp.iwmat.sawtter.modules

import com.google.inject.AbstractModule
import play.api.{ Configuration, Environment, Logger }

import jp.iwmat.sawtter.infrastructure.http._ws._
import jp.iwmat.sawtter.http._

class WSExternalModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def configure() = {
    bind(classOf[PageHttp]).to(classOf[PageHttpWS])
  }
}
