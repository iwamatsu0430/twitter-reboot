package jp.iwmat.sawtter.models.mails

trait MailData {
  def to: String
  def from: String
  def subject: String
  def text: String
}
