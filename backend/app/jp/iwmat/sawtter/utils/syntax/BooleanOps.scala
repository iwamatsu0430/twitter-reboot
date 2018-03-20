package jp.iwmat.sawtter.utils.syntax

import jp.iwmat.sawtter.models.Errors
import jp.iwmat.sawtter.repositories.{ RDB, Transaction }

trait BooleanOps {
  implicit class BooleanToTransactionOps(condition: Boolean) {
    def orElse(error: Errors)(implicit rdb: RDB): Transaction[_] =
      Transaction.either(condition) or error
  }
}
