package jp.iwmat.sawtter.base

import org.scalatest.mockito.MockitoSugar
import org.mockito.{ ArgumentMatchers, Mockito }
import org.mockito.verification._
import org.mockito.stubbing.{ OngoingStubbing, Stubber }

trait Mock extends MockitoSugar {

  def when[A](methodCall: A): OngoingStubbing[A] = Mockito.when(methodCall)

  def verify[A](mock: A, mode: VerificationMode): A = Mockito.verify(mock, mode)

  def doReturn(toBeReturned: Any, toBeReturnedNext: Object*): Stubber = Mockito.doReturn(toBeReturned, toBeReturnedNext: _*)

  def times(wantedNumberOfInvocations: Int): VerificationMode = Mockito.times(wantedNumberOfInvocations)

  def never(): VerificationMode = Mockito.never()

  def verify[A](mock: A): A = Mockito.verify(mock)

  def reset[A](mock: A): Unit = Mockito.reset(mock)

  def spy[A](`object`: A): A = Mockito.spy(`object`)

  def eq[A](value: A): A = ArgumentMatchers.eq(value)

  def anyValue[A]: A = ArgumentMatchers.any[A]()

  def anyBoolean: Boolean = ArgumentMatchers.anyBoolean()

  def anyByte: Byte = ArgumentMatchers.anyByte()

  def anyChar: Char = ArgumentMatchers.anyChar()

  def anyDouble: Double = ArgumentMatchers.anyDouble()

  def anyFloat: Float = ArgumentMatchers.anyFloat()

  def anyInt: Int = ArgumentMatchers.anyInt()

  def anyList[A]: List[A] = anyValue[List[A]]

  def anyLong: Long = ArgumentMatchers.anyLong()

  def anyMap[A, B]: Map[A, B] = anyValue[Map[A, B]]

  def anySet[A]: Set[A] = anyValue[Set[A]]

  def anySeq[A]: Seq[A] = anyValue[Seq[A]]

  def anyShort[A]: Short = ArgumentMatchers.anyShort()

  def anyString: String = ArgumentMatchers.anyString()
}
