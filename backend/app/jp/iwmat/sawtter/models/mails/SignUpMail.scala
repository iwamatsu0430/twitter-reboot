package jp.iwmat.sawtter.models.mails

import jp.iwmat.sawtter.models.SignUp
import jp.iwmat.sawtter.models.types.Email

case class SignUpMail(
  target: Email[SignUp],
  domain: String,
  host: String,
  token: String
) extends MailData {

  val to: String = target.value
  val from: String = s"info@$domain"
  val subject: String = "SAWTTERへようこそ！"
  val text: String = s"""SAWTTERへようこそ！

登録を完了するために以下のURLをクリックしてください！
$host/api/auth/verify/$token
"""
}
