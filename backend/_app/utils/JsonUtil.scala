package utils

import play.api.libs.json._

import controllers.ResponseCode._

/**
 * @author SAW
 */
object JsonUtil {

  def createJson[T](responseCodes: (Int, String), value: T = JsNull)(implicit writes: Writes[T]): JsValue = Json.toJson(Map(
    "code" -> Json.toJson(responseCodes._1),
    "reason" -> Json.toJson(responseCodes._2),
    "value" -> Json.toJson(value)
  ))

  def successJson: JsValue = Json.toJson(Map(
    "code" -> Json.toJson(NoReason._1),
    "reason" -> Json.toJson(NoReason._2),
    "value" -> JsNull
  ))
}
