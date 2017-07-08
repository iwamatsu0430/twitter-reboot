package utils

import java.security.MessageDigest

/**
 * @author SAW
 */
object HashUtil {

  def crypt(input: String): String = {
    val digestInstance = MessageDigest.getInstance("SHA-512")
    digestInstance.digest(input.getBytes).map("%02x".format(_)).mkString
  }
}
