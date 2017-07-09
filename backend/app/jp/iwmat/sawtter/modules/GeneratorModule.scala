package jp.iwmat.sawtter.modules

import com.google.inject.AbstractModule
import play.api.{ Configuration, Environment, Logger }

import jp.iwmat.sawtter.generators._

class GeneratorModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def configure() = {
    bind(classOf[Clocker]).to(classOf[ClockerImpl])
    bind(classOf[IdentifyBuilder]).to(classOf[IdentifyBuilderImpl])
    bind(classOf[Security]).to(classOf[SecurityImpl])
  }
}
