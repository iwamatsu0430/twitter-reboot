package jp.iwmat.sawtter.models.types

import org.scalatest._

class EmailSpec extends WordSpec with MustMatchers {
  // Minimal spec
  // Refer to https://www.w3.org/TR/html5/forms.html#valid-e-mail-address
  "isValid" must {

    "returns true by normal email" in {
      Email("info@sawtter.iwmat.jp").isValid mustBe true
    }

    "returns true by email with alias" in {
      Email("info+1@sawtter.iwmat.jp").isValid mustBe true
    }

    "returns true by minimal email" in {
      Email("info@sawtter").isValid mustBe true
    }

    "returns false by no at-mark" in {
      Email("infosawtter.iwmat.jp").isValid mustBe false
    }

    "returns false by empty" in {
      Email("").isValid mustBe false
    }
  }
}
