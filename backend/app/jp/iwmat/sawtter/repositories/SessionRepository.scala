package jp.iwmat.sawtter.repositories

import javax.inject.Inject

import jp.iwmat.sawtter.generators.{ Clocker, IdentifyBuilder, Security }
import jp.iwmat.sawtter.models._

class SessionRepository @Inject()(
  cache: Cache,
  identifyBuilder: IdentifyBuilder,
  security: Security,
  clocker: Clocker
) {

  def add(user: User): String = {
    val sessionKey = security.crypto(user.userId)
    ???
  }
}
