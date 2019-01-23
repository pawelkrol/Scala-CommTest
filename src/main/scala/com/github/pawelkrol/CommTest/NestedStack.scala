package com.github.pawelkrol.CommTest

import scala.collection.mutable.Stack

class NestedStack[T](init: Option[T]) {

  private var stack = new Stack[T]

  init match {
    case Some(value) => stack.push(value)
    case None =>
  }

  def all = stack.toList

  def any = stack.nonEmpty

  def peek = stack.top

  def pop = stack.pop

  def push(value: => T): Unit = { stack.push(value) }

  // override def toString = "[ " + stack.mkString(", ") + " ]"

  def closestMatch(predicate: T => Boolean) = stack.find(predicate(_))
}

object NestedStack {

  def apply[T]() = new NestedStack[T](None)

  def apply[T](init: T) = new NestedStack[T](Some(init))
}
