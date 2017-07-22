package jp.iwmat.sawtter.models

import java.time.ZonedDateTime

import org.scalatest._

import jp.iwmat.sawtter.models.types.{ Email, ID, Version }

class UserSpecBase extends WordSpec with MustMatchers {
  "isValidForSignUp" must {
    "returns true by empty user" in {
      User.isValidForSignUp(None) mustBe true
    }

    "returns true by disabled user" in {
      val user = User(
        userId = ID(1L),
        email = Email(""),
        status = UserStatus.Disabled,
        version = Version.init,
        updatedAt = ZonedDateTime.now,
        createdAt = ZonedDateTime.now
      )
      User.isValidForSignUp(Some(user)) mustBe true
    }

    "returns false by enabled user" in {
      val user = User(
        userId = ID(1L),
        email = Email(""),
        status = UserStatus.Enabled,
        version = Version.init,
        updatedAt = ZonedDateTime.now,
        createdAt = ZonedDateTime.now
      )
      User.isValidForSignUp(Some(user)) mustBe false
    }
  }
}
