package jp.iwmat.sawtter.generators

import javax.crypto._
import javax.crypto.spec.SecretKeySpec

trait Security {
  def crypto(source: Any): String
}

class SecurityImpl extends Security {
  // FIXME
  def crypto(source: Any) = {
    val algorithm = "AES"
    val secretKey = "foobar".getBytes

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, algorithm))
    cipher.doFinal(source.toString.getBytes).toString
  }
}
