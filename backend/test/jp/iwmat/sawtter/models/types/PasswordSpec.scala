package jp.iwmat.sawtter.models.types

import org.scalatest._

class PasswordSpec extends WordSpec with MustMatchers {
  "isValid" must {
    "returns true by '000001aA'" in {
      Password("000001aA").isValid mustBe true
    }

    "returns true by '000000000000000000000000000001aA' (32 chars)" in {
      Password("000000000000000000000000000001aA").isValid mustBe true
    }

    "returns false by '00001aA' 7 chars)" in {
      Password("00001aA").isValid mustBe false
    }

    "returns true by '0000000000000000000000000000001aA' (33 chars)" in {
      Password("0000000000000000000000000000001aA").isValid mustBe false
    }

    "returns false by '11111111'" in {
      Password("11111111").isValid mustBe false
    }

    "returns false by 'aaaaaaaa'" in {
      Password("aaaaaaaa").isValid mustBe false
    }

    "returns false by 'AAAAAAAA'" in {
      Password("AAAAAAAA").isValid mustBe false
    }

    "returns false by ''" in {
      Password("").isValid mustBe false
    }
  }
}
