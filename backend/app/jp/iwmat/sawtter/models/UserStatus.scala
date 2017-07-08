package jp.iwmat.sawtter.models

sealed abstract class UserStatus(val value: String) extends Enum[String]
object UserStatus extends EnumCompanion[String, UserStatus] {
  case object Enabled extends UserStatus("ENA")
  case object Disabled extends UserStatus("DIS")
  case object Registered extends UserStatus("REG")

  val values = Seq(Enabled, Disabled, Registered)
}
