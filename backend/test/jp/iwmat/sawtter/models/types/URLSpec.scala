package jp.iwmat.sawtter.models.types

import org.scalatest._

class URLSpec extends WordSpec with MustMatchers {
  "isValid" must {
    "returns true starts with http" in {
      URL("http://foo").isValid mustBe true
    }

    "returns true starts with https" in {
      URL("https://foo").isValid mustBe true
    }

    "returns true starts with others" in {
      URL("foo").isValid mustBe false
    }
  }
}
