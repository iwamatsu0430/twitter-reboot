package jp.iwmat.sawtter.utils.generators

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID
import javax.xml.bind.DatatypeConverter

trait IdentifyBuilder {
  def generate(): Long
  def generateUUID(): String
  def hash(value: String): String
}

class IdentifyBuilderImpl extends IdentifyBuilder {

  def generate() = Math.abs(UUID.randomUUID.getMostSignificantBits)

  def generateUUID() = UUID.randomUUID.toString

  def hash(value: String) = {
    val bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))
    DatatypeConverter.printHexBinary(bytes).toLowerCase
  }
}
