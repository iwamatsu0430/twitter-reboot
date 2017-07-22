package jp.iwmat.sawtter.controllers.mappers

import play.api.libs.json._
import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.models.types._

trait MapperBase {

  implicit def typeStringReads[A, B[A]](implicit iso: Iso[String, B[A]]): Reads[B[A]] = new Reads[B[A]] {
    def reads(json: JsValue): JsResult[B[A]] = {
      json.validate[String].map(s => iso.to(s))
    }
  }

  implicit val unitWrites: Writes[Unit] = new Writes[Unit] {
    def writes(value: Unit): JsValue = JsNull
  }
}
