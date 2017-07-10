package jp.iwmat.sawtter.modules

import javax.inject.Inject

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import play.api.{ Configuration, Environment, Logger }

import jp.iwmat.sawtter.generators._

class GeneratorModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def keyException(key: String) = throw new Exception(s"configure key not found. key=$key")

  val cryptoKey = "play.crypto.secret"

  def configure() = {
    bind(classOf[Clocker]).to(classOf[ClockerImpl])
    bind(classOf[IdentifyBuilder]).to(classOf[IdentifyBuilderImpl])
    bind(classOf[String])
      .annotatedWith(Names.named(cryptoKey))
      .toInstance(
        configuration
          .getString(cryptoKey)
          .getOrElse(keyException(cryptoKey))
      )
    bind(classOf[Security]).to(classOf[SecurityImpl])
  }
}
