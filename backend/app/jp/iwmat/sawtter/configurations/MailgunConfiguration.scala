package jp.iwmat.sawtter.configurations

import javax.inject.Inject

import play.api.Configuration

class MailgunConfiguration @Inject()(val conf: Configuration) extends ConfigurationBase {
  val url = getString("mailgun.url")
  val user = getString("mailgun.user")
  val key = getString("mailgun.key")
}
