package jp.iwmat.sawtter.infrastructure.mail._mock

import jp.iwmat.sawtter.models.mails._
import jp.iwmat.sawtter.utils.Mailer

class MockMailer extends Mailer {

  def send(mailData: MailData): Unit = {
    logger.debug(s"============ Local Mailer ============")
    logger.debug(s"from: ${mailData.from.value}")
    logger.debug(s"to: ${mailData.to.value}")
    logger.debug(s"subject: ${mailData.subject}")
    logger.debug(s"text: ${mailData.text}")
  }
}
