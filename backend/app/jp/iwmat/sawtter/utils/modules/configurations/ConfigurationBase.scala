package jp.iwmat.sawtter.utils.modules.configurations

import play.api.Configuration

trait ConfigurationBase {

  class ConfigurationException(path: String) extends Throwable {
    override def getMessage = s"Configuration value '$path' Not found."
  }

  def configuration: Configuration

  def getString(path: String): String = {
    configuration.getString(path).getOrElse(throw new ConfigurationException(path))
  }
}
