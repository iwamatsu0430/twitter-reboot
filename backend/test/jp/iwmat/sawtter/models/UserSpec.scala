package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

import org.scalatest._

class UserSpecBase extends WordSpec with MustMatchers {
  "isValidForSignUp" must {
    "returns true by empty user" in {
      User.isValidForSignUp(None) mustBe true
    }

    "returns true by disabled user" in {
      val user = User(
        userId = 1L,
        email = "",
        status = UserStatus.Disabled,
        version = 1L,
        updatedAt = ZonedDateTime.now,
        createdAt = ZonedDateTime.now
      )
      User.isValidForSignUp(Some(user)) mustBe true
    }

    "returns false by enabled user" in {
      val user = User(
        userId = 1L,
        email = "",
        status = UserStatus.Enabled,
        version = 1L,
        updatedAt = ZonedDateTime.now,
        createdAt = ZonedDateTime.now
      )
      User.isValidForSignUp(Some(user)) mustBe false
    }
  }
}
