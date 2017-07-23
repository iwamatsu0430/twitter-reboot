package jp.iwmat.sawtter.repositories

import play.api.Logger

import jp.iwmat.sawtter.models.mails.MailData

trait Mailer {

  val logger = Logger(this.getClass)

  def send(mailData: MailData): Unit

}
