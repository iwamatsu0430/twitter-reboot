package jp.iwmat.sawtter.generators

trait IdentifyBuilder {
  def generate(): Long
  def generateUUID(): String
  def hash(value: String): String
}

// FIXME
class IdentifyBuilderImpl extends IdentifyBuilder {
  def generate() = 123L
  def generateUUID() = "aaa"
  def hash(value: String) = "aaa"
}
