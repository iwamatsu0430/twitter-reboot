package utils

/**
 * @author SAW
 */
case class Mail(text: String) {

  def sendTo(to: String): Unit = {
    // TODO SAW send mail
    println(s"To: $to")
    println(s"$text")
  }
}

/**
 * @author SAW
 */
object MailUtil {

  def createSignUpMessage(url: String): Mail = create(
    s"""
      |Hi
      |
      |Just now, your account created.
      |
      |$url
      |Please go to this page and confirm your mail address.
      |
      |Thank you.
    """.stripMargin)

  def create(text: String): Mail = Mail(text)
}
