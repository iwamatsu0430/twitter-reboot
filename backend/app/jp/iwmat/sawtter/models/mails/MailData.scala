package jp.iwmat.sawtter.models.mails

import jp.iwmat.sawtter.models.types.Email

trait MailData {
  def to: Email[_]
  def from: Email[_]
  def subject: String
  def text: String
}
