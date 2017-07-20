package jp.iwmat.sawtter.models

import scalaz.\/

trait Enum[A] {
  def value: A
}

trait EnumCompanion[A, B <: Enum[A]] {
  def values: Seq[B]
  def valueOf(value: A): Errors \/ B = values
    .find(_.value == value)
    .map(\/.right)
    .getOrElse(\/.left(Errors.EnumNotFound(value)))
}

object Enum {

  object writes {
    import play.api.libs.json._

    implicit def enumStringWrites[B <: Enum[String]](implicit w: Writes[String]): Writes[B] = new Writes[B] {
      def writes(enum: B): JsValue = {
        Json.toJson(enum.value)
      }
    }

    implicit def enumIntWrites[B <: Enum[Int]](implicit w: Writes[Int]): Writes[B] = new Writes[B] {
      def writes(enum: B): JsValue = {
        Json.toJson(enum.value)
      }
    }

    implicit def enumLongWrites[B <: Enum[Long]](implicit w: Writes[Long]): Writes[B] = new Writes[B] {
      def writes(enum: B): JsValue = {
        Json.toJson(enum.value)
      }
    }

    implicit def enumBooleanWrites[B <: Enum[Boolean]](implicit w: Writes[Boolean]): Writes[B] = new Writes[B] {
      def writes(enum: B): JsValue = {
        Json.toJson(enum.value)
      }
    }
  }
}
