package jp.iwmat.sawtter.utils.modules

import com.google.inject.AbstractModule
import play.api.{ Configuration, Environment, Logger }

import jp.iwmat.sawtter.infrastructure.mail._mock._
import jp.iwmat.sawtter.utils.Mailer

class MockMailerModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def configure() = {
    bind(classOf[Mailer]).to(classOf[MockMailer])
  }
}
