package jp.iwmat.sawtter.utils.http

import jp.iwmat.sawtter._
import jp.iwmat.sawtter.models.types.URL

trait PageHttp {
  def canIFrame(url: URL[_]): Result[Boolean]
}
