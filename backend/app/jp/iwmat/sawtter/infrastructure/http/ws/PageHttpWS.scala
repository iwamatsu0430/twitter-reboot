package jp.iwmat.sawtter.infrastructure.http._ws

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import play.api.libs.ws.WSClient

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.http.PageHttp

class PageHttpWS @Inject()(
  ws: WSClient
)(
  implicit
  ec: ExecutionContext
) extends PageHttp {
  def canIFrame(url: String): Result[Boolean] = {
    val future = ws
      .url(url)
      .get
      .map { response =>
        !response.allHeaders.exists(_._1.toLowerCase == "x-frame-options")
      }
    Result(future)
  }
}
