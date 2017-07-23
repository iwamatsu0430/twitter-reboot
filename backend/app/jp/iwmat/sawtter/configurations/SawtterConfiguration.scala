package jp.iwmat.sawtter.configurations

import javax.inject.Inject

import play.api.Configuration

class SawtterConfiguration @Inject()(val conf: Configuration) extends ConfigurationBase {

  case class Hosts(frontend: String, backend: String)

  lazy val domain = getString("sawtter.domain")

  lazy val hosts = Hosts(
    getString("sawtter.hosts.frontend"),
    getString("sawtter.hosts.backend")
  )
}
