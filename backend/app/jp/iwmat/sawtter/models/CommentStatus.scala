package jp.iwmat.sawtter.models

sealed abstract class CommentStatus(val value: String) extends Enum[String]
object CommentStatus extends EnumCompanion[String, CommentStatus] {
  case object Alived extends CommentStatus("ALV")
  case object Deleted extends CommentStatus("DEL")

  val values = Seq(Alived, Deleted)
}
