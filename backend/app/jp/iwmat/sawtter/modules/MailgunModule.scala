package jp.iwmat.sawtter.modules

import com.google.inject.AbstractModule
import play.api.{ Configuration, Environment, Logger }

import jp.iwmat.sawtter.infrastructure.mail._mailgun._
import jp.iwmat.sawtter.repositories._

class MailgunModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def configure() = {
    bind(classOf[Mail]).to(classOf[MailgunMail])
  }
}
