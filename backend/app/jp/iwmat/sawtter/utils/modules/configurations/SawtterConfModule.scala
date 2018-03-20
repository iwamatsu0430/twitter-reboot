package jp.iwmat.sawtter.utils.modules.configurations

import com.google.inject.AbstractModule

import play.api.{ Configuration, Environment }

import jp.iwmat.sawtter.models.configurations.{ SawtterConf, SawtterHosts }

class SawtterConfModule(
  val environment: Environment,
  val configuration: Configuration
) extends AbstractModule with ConfigurationBase {

  def configure() = {
    lazy val domain = getString("sawtter.domain")
    lazy val hosts = SawtterHosts(
      getString("sawtter.hosts.frontend"),
      getString("sawtter.hosts.backend")
    )
    bind(classOf[SawtterConf]).toInstance(
      SawtterConf(
        domain,
        hosts
      )
    )
  }
}
