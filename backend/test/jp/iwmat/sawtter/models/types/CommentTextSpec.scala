package jp.iwmat.sawtter.models.types

import org.scalatest._

class CommentTextSpec extends WordSpec with MustMatchers {
  "isValid" must {
    "returns true 1 char text" in {
      CommentText("a").isValid mustBe true
    }

    "returns true 140 chars text" in {
      CommentText("a" * 140).isValid mustBe true
    }

    "returns false empty text" in {
      CommentText("").isValid mustBe false
    }

    "returns false 141 chars text" in {
      CommentText("a" * 141).isValid mustBe false
    }
  }
}
