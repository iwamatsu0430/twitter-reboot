package jp.iwmat.sawtter.controllers.mappers

import play.api.libs.json._

import jp.iwmat.sawtter.models.types._

trait MapperBase extends TypeReads with TypeWrites {
  implicit val unitWrites: Writes[Unit] = new Writes[Unit] {
    def writes(value: Unit): JsValue = JsNull
  }
}
