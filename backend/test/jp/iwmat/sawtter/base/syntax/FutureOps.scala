package jp.iwmat.sawtter.base.syntax

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.Duration

trait FutureOps {
  implicit class FutureToOps[A](future: Future[A]) {
    def await()(implicit ec: ExecutionContext): A = {
      Await.result(future, Duration.Inf)
    }
  }
}
