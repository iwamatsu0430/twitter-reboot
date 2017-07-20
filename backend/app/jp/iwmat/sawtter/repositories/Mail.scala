package jp.iwmat.sawtter.repositories

import jp.iwmat.sawtter.models.MailData

trait Mail {
  def send(mailData: MailData): Unit
}
