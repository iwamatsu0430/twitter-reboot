package jp.iwmat.sawtter.infrastructure.mail._mailgun

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import play.api.Configuration
import play.api.libs.ws.{ WSAuthScheme, WSClient }

import jp.iwmat.sawtter.models._
import jp.iwmat.sawtter.repositories._

class MailgunMail @Inject()(
  conf: Configuration,
  ws: WSClient
)(
  implicit
  ec: ExecutionContext
) extends Mail {
  def send(mailData: MailData): Unit = {
    val url = conf.getString("mailgun.url").getOrElse("") // FIXME
    val user = conf.getString("mailgun.user").getOrElse("") // FIXME
    val key = conf.getString("mailgun.key").getOrElse("") // FIXME
    val body = Map(
      "from" -> Seq(s"SAWTTER 事務局 <${mailData.from}>"),
      "h:Sender" -> Seq(mailData.from),
      "to" -> Seq(mailData.to),
      "subject" -> Seq(mailData.subject),
      "text" -> Seq(mailData.text)
    )
    ws
      .url(url)
      .withAuth(user, key, WSAuthScheme.BASIC)
      .withRequestTimeout(10 seconds)
      .post(body)
      .onComplete {
        case scala.util.Success(s) => println(s"status: ${s.status}, body: ${s.body}")
        case scala.util.Failure(f) => println(s"success: $f")
      }
  }
}
