package jp.iwmat.sawtter.controllers

import org.scalatest.{ BeforeAndAfter, MustMatchers, WordSpec }
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import jp.iwmat.sawtter.base.DBSpecBase

class AuthControllerSpec extends WordSpec with MustMatchers {
  "hello" must {
    "world" in {
      1 mustBe 1
    }
  }
}

// class FooSpec extends DBSpecBase with BeforeAndAfter {
//   import slick.driver.MySQLDriver.api._
//
//   import jp.iwmat.sawtter.repositories._
//   import jp.iwmat.sawtter.infrastructure.jdbc._slick._
//
//   def insert(userId: Long, mail: String): DBIOResult[Int] = {
//     DBIOResult(sqlu"""insert into users (user_id, email, password, status, version, updated_at, created_at) values ($userId, $mail, 'password', 'ENA', 1, '2017-07-01 00:00:00', '2017-07-01 00:00:00')""")
//   }
//
//   before {
//     val result = for {
//       _ <- begin()
//       _ <- insert(1L, "a@sutead.com")
//       _ <- insert(2L, "b@sutead.com")
//       _ <- insert(3L, "c@sutead.com")
//     } yield ()
//     execDB(result)
//   }
//
//   after {
//     execDB(rollback())
//   }
//
//   "foo" must {
//     "bar" in {
//     }
//   }
// }
