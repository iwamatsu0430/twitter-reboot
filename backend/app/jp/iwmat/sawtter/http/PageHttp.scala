package jp.iwmat.sawtter.externals

import jp.iwmat.sawtter._

trait PageHttp {
  def confirm(url: String): Result[Boolean]
}
