package jp.iwmat.sawtter.http

import jp.iwmat.sawtter._

trait PageHttp {
  def confirm(url: String): Result[Boolean]
}
