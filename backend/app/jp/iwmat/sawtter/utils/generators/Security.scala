package jp.iwmat.sawtter.utils.generators

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.{ Arrays => JArrays }
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.{ Inject, Named }
import org.apache.commons.codec.binary.Base64

trait Security {
  def encrypt(source: String): String
  def decrypt(encrypted: String): String
}

class SecurityImpl @Inject()(
  @Named("play.crypto.secret") secretKey: String
) extends Security {

  val algorithm = "AES/ECB/PKCS5Padding"

  val charset = StandardCharsets.UTF_8

  val key = {
    var keyBytes: Array[Byte] = secretKey.getBytes(charset)
    val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
    keyBytes = sha.digest(keyBytes)
    keyBytes = JArrays.copyOf(keyBytes, 16)
    new SecretKeySpec(keyBytes, "AES")
  }

  def encrypt(value: String): String = {
    val cipher: Cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, key)
    Base64.encodeBase64String(cipher.doFinal(value.getBytes(charset)))
  }

  def decrypt(encrypted: String): String = {
    val cipher: Cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.DECRYPT_MODE, key)
    new String(cipher.doFinal(Base64.decodeBase64(encrypted)))
  }
}
