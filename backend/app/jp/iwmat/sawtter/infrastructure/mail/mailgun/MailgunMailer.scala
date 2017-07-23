package jp.iwmat.sawtter.infrastructure.mail._mailgun

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import play.api.libs.ws.{ WSAuthScheme, WSClient }

import jp.iwmat.sawtter.configurations.MailgunConfiguration
import jp.iwmat.sawtter.models.mails._
import jp.iwmat.sawtter.repositories._

class MailgunMailer @Inject()(
  conf: MailgunConfiguration,
  ws: WSClient
)(
  implicit
  ec: ExecutionContext
) extends Mailer {

  def send(mailData: MailData): Unit = {
    val body = Map(
      "from" -> Seq(s"SAWTTER 事務局 <${mailData.from.value}>"),
      "h:Sender" -> Seq(mailData.from.value),
      "to" -> Seq(mailData.to.value),
      "subject" -> Seq(mailData.subject),
      "text" -> Seq(mailData.text)
    )
    ws
      .url(conf.url)
      .withAuth(conf.user, conf.key, WSAuthScheme.BASIC)
      .withRequestTimeout(10 seconds)
      .post(body)
      .onComplete {
        case scala.util.Success(s) => logger.debug(s"Mailgun response >>> status: ${s.status}, body: ${s.body}")
        case scala.util.Failure(f) => logger.debug(s"Mailgun response >>> failure: $f")
      }
  }
}
