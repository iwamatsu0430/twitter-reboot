package jp.iwmat.sawtter.utils.modules.configurations

import com.google.inject.AbstractModule

import play.api.{ Configuration, Environment }

import jp.iwmat.sawtter.models.configurations.MailgunConf

class MailgunConfModule(
  val environment: Environment,
  val configuration: Configuration
) extends AbstractModule with ConfigurationBase {

  def configure() = {
    lazy val url = getString("mailgun.url")
    lazy val user = getString("mailgun.user")
    lazy val key = getString("mailgun.key")
    bind(classOf[MailgunConf]).toInstance(
      MailgunConf(
        url,
        user,
        key
      )
    )
  }
}
