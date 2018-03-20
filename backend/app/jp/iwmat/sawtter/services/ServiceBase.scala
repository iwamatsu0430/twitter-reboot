package jp.iwmat.sawtter.services

import scalaz.syntax.std.ToOptionOps

import jp.iwmat.sawtter.utils.syntax.{ BooleanOps, OptionOps }

trait ServiceBase
  extends BooleanOps
  with OptionOps
  with ToOptionOps
