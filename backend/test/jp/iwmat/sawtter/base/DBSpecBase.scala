package jp.iwmat.sawtter.base

import scala.concurrent.{ Await, ExecutionContext }
import scala.concurrent.duration.Duration

import org.scalatestplus.play.{ PlaySpec, OneAppPerSuite }
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import scalaz.\/
import slick.driver.MySQLDriver.api._

import jp.iwmat.sawtter.infrastructure.jdbc._slick._
import jp.iwmat.sawtter.repositories._
import jp.iwmat.sawtter.models.Errors

trait DBSpecBase extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(Map(
        "db.default.driver" -> "org.h2.Driver",
        "db.default.url" -> "jdbc:h2:mem:play;DB_CLOSE_DELAY=-1",
        "db.default.username" -> "root",
        "db.default.password" -> "",
        "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
        "slick.dbs.default.db.driver" -> "org.h2.Driver",
        "slick.dbs.default.db.url" -> "jdbc:h2:mem:play"
      ))
      .build()

  implicit lazy val rdb: RDB = app.injector.instanceOf[RDB]

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  def execDB[A](result: DBResult[A]): Errors \/ A = {
    val future = rdb.exec(result).run
    Await.result(future, Duration.Inf)
  }

  def begin(): DBIOResult[Int] = DBIOResult(sqlu"begin")

  def rollback(): DBIOResult[Int] = DBIOResult(sqlu"rollback")
}
