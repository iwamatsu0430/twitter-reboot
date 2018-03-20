package jp.iwmat.sawtter.models.mails

import jp.iwmat.sawtter.models.{ SignUp, UserToken }
import jp.iwmat.sawtter.models.configurations.SawtterConf
import jp.iwmat.sawtter.models.types.{ Email, Token }

case class SignUpMail(
  target: Email[SignUp],
  domain: String,
  host: String,
  token: Token[UserToken]
) extends MailData {

  val to: Email[_] = target
  val from: Email[_] = Email(s"info@$domain")
  val subject: String = "SAWTTERへようこそ！"
  val text: String = s"""SAWTTERへようこそ！

登録を完了するために以下のURLをクリックしてください！
$host/api/auth/verify/${token.value}
"""
}

object SignUpMail {
  def create(
    email: Email[SignUp],
    token: Token[UserToken],
    conf: SawtterConf
  ): SignUpMail = SignUpMail(
    email,
    conf.domain,
    conf.hosts.backend,
    token
  )
}
