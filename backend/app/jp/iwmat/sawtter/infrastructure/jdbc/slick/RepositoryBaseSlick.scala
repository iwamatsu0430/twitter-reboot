package jp.iwmat.sawtter.infrastructure.jdbc._slick

import java.time._
import java.time.format.DateTimeFormatter

import slick.driver.MySQLDriver.API
import slick.jdbc.{ GetResult, PositionedParameters, PositionedResult, SetParameter }

import jp.iwmat.sawtter.models.{ Enum, Iso }

trait RepositoryBaseSlick extends API {

  class SetDateTime extends SetParameter[ZonedDateTime] {
    def apply(dt: ZonedDateTime, pp: PositionedParameters) {
      pp.setString(dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")))
    }
  }

  class GetDateTime extends GetResult[ZonedDateTime] {
    def apply(rs: PositionedResult): ZonedDateTime = {
      val datetimeStr = rs.nextString()
      val ldt = LocalDateTime.parse(datetimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
      ldt.atZone(ZoneOffset.UTC)
    }
  }

  class GetLongType[A, B[A]](implicit iso: Iso[Long, B[A]]) extends GetResult[B[A]] {
    def apply(rs: PositionedResult): B[A] = {
      iso.to(rs.nextLong())
    }
  }

  implicit val setDateTime = new SetDateTime
  implicit val getDateTime = new GetDateTime
}
