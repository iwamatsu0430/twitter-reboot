package jp.iwmat.sawtter.configurations

import javax.inject.Inject

import play.api.Configuration

class SawtterConfiguration @Inject()(val conf: Configuration) extends ConfigurationBase {

  case class Hosts(frontend: String, backend: String)

  val domain = getString("sawtter.domain")

  val hosts = Hosts(
    getString("sawtter.hosts.frontend"),
    getString("sawtter.hosts.backend")
  )
}
