package com.github.pawelkrol.CommTest

class FromToValidator[T](code: () => T, predicate: () => T, initial: () => T) {

  def to(ending: => T): Unit = {
    val gotInitial = predicate()
    assert(gotInitial == initial(), "expected that '%s' changes to '%s', but it changed from '%s'".format(initial(), ending, gotInitial))
    code()
    val gotEnding = predicate()
    assert(gotEnding == ending, "expected that '%s' changes to '%s', but it changed to '%s'".format(initial(), ending, gotEnding))
  }
}

object FromToValidator {

  def apply[T](code: () => Any, predicate: () => T, initial: () => T) = new FromToValidator(code, predicate, initial)
}
