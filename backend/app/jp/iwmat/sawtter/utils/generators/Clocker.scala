package jp.iwmat.sawtter.utils.generators

import java.time.{ ZonedDateTime, ZoneOffset }

trait Clocker {
  def now: ZonedDateTime
}

class ClockerImpl extends Clocker {
  def now = ZonedDateTime.now(ZoneOffset.UTC)
}
