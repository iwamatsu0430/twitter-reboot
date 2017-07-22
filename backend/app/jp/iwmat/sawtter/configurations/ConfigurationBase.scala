package jp.iwmat.sawtter.configurations

import play.api.Configuration

trait ConfigurationBase {

  class ConfigurationException(path: String) extends Throwable {
    override def getMessage = s"Configuration value '$path' Not found."
  }

  def conf: Configuration

  def getString(path: String): String = {
    conf.getString(path).getOrElse(throw new ConfigurationException(path))
  }
}
