package com.github.pawelkrol.CommTest

class Expectation[T](code: () => T) {

  private def validateNoChange[T](code: () => T, predicate: () => T) {
    val initial = predicate()
    code()
    val ending = predicate()
    assert(initial == ending, "expected that '%s' does not change, but it changed to '%s'".format(initial, ending))
  }

  def toChange[T](predicate: => T) = ChangeValidator(code, () => predicate)

  def notToChange[T](predicate: => T) = validateNoChange(code, () => predicate)
}

object Expectation {

  def apply[T](code: () => T) = new Expectation(code)
}
