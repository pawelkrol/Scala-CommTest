package com.github.pawelkrol.CommTest

class ChangeValidator[T](code: () => Any, predicate: () => T) {

  def from(initial: => T) = FromToValidator(code, predicate, () => initial)

  def to(value: => T) = {
    code()
    val ending = predicate()
    assert(ending == value, "expected change to '%s', but it changed to '%s'".format(value, ending))
  }
}

object ChangeValidator {

  def apply[T](code: () => Any, predicate: () => T) = new ChangeValidator(code, predicate)
}
