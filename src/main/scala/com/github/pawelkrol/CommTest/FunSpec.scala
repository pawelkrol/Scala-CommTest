package com.github.pawelkrol.CommTest

import java.util.NoSuchElementException

import org.scalactic.source.Position
import org.scalatest.Tag

import scala.collection.mutable.HashMap
import scala.language.existentials

trait FunSpec extends ExtendedCPU6502Spec {

  private var _outputPrg: String = _

  protected def outputPrg: String = _outputPrg

  protected def outputPrg_=(fileName: String) {
    _outputPrg = fileName
    _programData = ProgramData(fileName)
  }

  private var _programData: ProgramData = _

  private val beforeFilter = NestedStack[NestedStack[() => Unit]](NestedStack[() => Unit]())

  protected def before(procedure: => Unit) {
    beforeFilter.peek.push(() => procedure)
  }

  private val descriptionBuffer = NestedStack[String]()

  private var describedSubroutine: Option[String] = _

  override protected def describe(description: String)(fun: => Unit)(implicit pos: Position): Unit = {
    beforeFilter.push(NestedStack())
    descriptionBuffer.push(description)
    examples.push(HashMap())
    lets.push(HashMap())
    super.describe(description)(fun)(pos)
    beforeFilter.pop
    descriptionBuffer.pop
    examples.pop
    lets.pop
  }

  protected def context(description: String)(fun: => Unit)(implicit pos: Position): Unit = {
    describe(description)(fun)(pos)
  }

  private val ignoreTests = NestedStack[Boolean]()

  protected def xdescribe(description: String)(fun: => Unit)(implicit pos: Position): Unit = {
    ignoreTests.push(true)
    describe(description)(fun)(pos)
    ignoreTests.pop
  }

  private def test(
    before: List[() => Unit],
    description: Option[String],
    letValues: List[HashMap[String, Tuple2[Class[_], Any]]],
    testFun: => Any /* Assertion */
  ): () => Any = () => {
    describedSubroutine = description
    memoisedLets = letValues
    initCore(_programData)
    before.foreach(_())
    testFun
  }

  protected class NestedItWord extends ItWord {
    override def apply(specText: String, testTags: Tag*)(testFun: => Any /* Assertion */)(implicit pos: Position): Unit = {
      if (ignoreTests.any)
        xit(specText, testTags: _*)(testFun)(pos)
      else {
        val before = beforeFilter.all.reverse.map(_.all.reverse).flatten
        val description = descriptionBuffer.closestMatch((item) => labelLog.contains(item))
        val letValues = lets.all
        super.apply(specText, testTags: _*)(test(before, description, letValues, testFun)())(pos)
      }
    }
  }

  override protected val it = new NestedItWord

  protected def xit(specText: String, testTags: Tag*)(testFun: => Any /* Assertion */)(implicit pos: Position): Unit = {
    ignore(specText, testTags: _*)(testFun)(pos)
  }

  private def emulateOpJSR(address: Short) {
    SP -= 2
    PC = address
  }

  private def callSubroutine(address: Short) {
    val stackPointer = SP
    emulateOpJSR(address)
    while (SP != stackPointer) {
      core.executeInstruction
    }
  }

  protected def call(name: String) {
    if (describedSubroutine === null)
      throw new UnsupportedOperationException("Subroutine call not allowed outside of an example scope")

    val subroutine = label2address(name)

    callSubroutine(subroutine)
  }

  protected def call {
    call(subroutineName)
  }

  private def subroutineName =
    describedSubroutine match {
      case Some(name) =>
        name
      case None =>
        throw new UnsupportedOperationException("No subroutine to call specified")
    }

  private var examples: NestedStack[HashMap[String, () => Unit]] = NestedStack(HashMap())

  protected def includeExamples(name: String) {
    examples.all.foldLeft[Option[() => Unit]](None)((result, scopedExamples) => {
      result match {
        case Some(_) =>
          result
        case None => {
          scopedExamples.get(name) match {
            case Some(procedure) =>
              Some(procedure)
            case None =>
              result
          }
        }
      }
    }) match {
      case Some(procedure) =>
        describe(name) {
          procedure()
        }
      case None =>
        throw new NoSuchElementException("Undefined shared examples name '" + name + "'")
    }
  }

  protected def sharedExamples(name: String)(procedure: => Unit) {
    examples.peek.put(name, () => procedure)
  }

  private val lets: NestedStack[HashMap[String, Tuple2[Class[_], Any]]] = NestedStack(HashMap())

  private var memoisedLets: List[HashMap[String, Tuple2[Class[_], Any]]] = _

  protected def let(name: String)(value: => Any) {
    lets.peek.put(name, (value.getClass, value))
  }

  protected def get(name: String) = {
    val (className, assignmentValue) = memoisedLets.find((scopedLets) => scopedLets.contains(name)) match {
      case Some(value) =>
        value(name)
      case None =>
        throw new NoSuchElementException("Undefined variable '" + name + "' (did you forget to say 'let(\"" + name + "\") { ... }'?)")
    }

    className.cast(assignmentValue)
  }

  def expect[T](code: => T) = Expectation(() => code)
}
