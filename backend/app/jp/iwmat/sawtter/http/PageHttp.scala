package jp.iwmat.sawtter.http

import jp.iwmat.sawtter._

trait PageHttp {
  def canIFrame(url: String): Result[Boolean]
}
