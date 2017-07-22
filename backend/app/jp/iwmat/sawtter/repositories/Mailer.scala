package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models.mails.MailData

trait Mailer {
  def send(mailData: MailData): Unit
}
