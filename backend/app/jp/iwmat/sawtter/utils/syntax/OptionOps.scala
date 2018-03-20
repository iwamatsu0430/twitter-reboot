package jp.iwmat.sawtter.utils.syntax

import jp.iwmat.sawtter.models.Errors
import jp.iwmat.sawtter.repositories.{ RDB, Transaction }

trait OptionOps {
  implicit class OptionToTransactionOps[A](option: Option[A]) {
    def getOr(error: Errors)(implicit rdb: RDB): Transaction[A] =
      Transaction.getOrElse(option)(error)
  }
}
