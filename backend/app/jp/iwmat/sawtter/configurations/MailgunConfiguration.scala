package jp.iwmat.sawtter.configurations

import javax.inject.Inject

import play.api.Configuration

class MailgunConfiguration @Inject()(val conf: Configuration) extends ConfigurationBase {
  lazy val url = getString("mailgun.url")
  lazy val user = getString("mailgun.user")
  lazy val key = getString("mailgun.key")
}
