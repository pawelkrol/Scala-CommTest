package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.{ Application, Core, Register }
import com.github.pawelkrol.CPU6502.{ CommodoreMemory, Memory => CMemory, SimpleMemory }
import com.github.pawelkrol.CPU6502.Commodore.ExtendedMemory

import java.io.FileNotFoundException
import java.lang.Character.{ isLetterOrDigit, isWhitespace }
import java.util.NoSuchElementException

import org.scalatest.{ FunSpec => SFunSpec }

import Memory.{ Commodore64C, OnlyRAM, Type => MemoryType }
import MiscUtils._

trait CPU6502Spec extends SFunSpec {

  private var _labelLog: LabelLog = LabelLog()

  private var _memoryType: MemoryType = Commodore64C

  private var _plus60kEnabled = false

  lazy protected val memory: CMemory =
    _memoryType match {
      case Commodore64C =>
        if (plus60kEnabled)
          ExtendedMemory()
        else
          CommodoreMemory()
      case OnlyRAM =>
        SimpleMemory()
    }

  lazy protected val register: Register = core.register

  lazy protected val core = Core(memory)

  protected def initRegisters: Unit

  protected def initCore(programData: ProgramData): Unit = {
    core.reset
    memory.init
    initRegisters
    if (programData != null) {
      register.setPC(programData.address)
      memory.write(programData.address, programData.program)
    }
  }

  private def processAfterLabelBeforeSign(char: Char, name: String, label: String, afterLabel: Boolean, hex: Option[Boolean], sign: Option[Boolean], offset: Option[String]): Tuple5[String, Boolean, Option[Boolean], Option[Boolean], Option[String]] =
    if (isWhitespace(char))
      (label, afterLabel, hex, sign, offset)
    else
      char match {
        case '+' =>
          (label, afterLabel, hex, Some(true), offset)
        case '-' =>
          (label, afterLabel, hex, Some(false), offset)
        case _ =>
          throw new NumberFormatException("Invalid label '" + name + "'")
      }

  private def processAfterSignBeforeAddress(char: Char, digits: String, name: String, label: String, afterLabel: Boolean, hex: Option[Boolean], sign: Option[Boolean]): Tuple5[String, Boolean, Option[Boolean], Option[Boolean], Option[String]] =
    if (isLetterOrDigit(char))
      (label, afterLabel, hex, sign, Some(digits + char))
    else
      throw new NumberFormatException("Invalid label '" + name + "'")

  protected def label2address(name: String) = {
    val (label, afterLabel, hex, sign, offset) =
      name.trim.toList.foldLeft[Tuple5[String, Boolean, Option[Boolean], Option[Boolean], Option[String]]](("", false, None, None, None))((result, char) => {
        val (label, afterLabel, hex, sign, offset) = result
          if (afterLabel)
            sign match {
              case Some(_) =>
                offset match {
                  case Some(digits) =>
                    processAfterSignBeforeAddress(char, digits, name, label, afterLabel, hex, sign)
                  case None =>
                    if (isWhitespace(char))
                      (label, afterLabel, hex, sign, offset)
                    else
                      if (char == '$')
                        (label, afterLabel, Some(true), sign, Some(""))
                      else
                        processAfterSignBeforeAddress(char, "", name, label, afterLabel, Some(false), sign)
                }
              case None =>
                processAfterLabelBeforeSign(char, name, label, afterLabel, hex, sign, offset)
            }
          else
            if (isLetterOrDigit(char) || char == '_')
              (label + char, afterLabel, hex, sign, offset)
            else
              processAfterLabelBeforeSign(char, name, label, true, hex, sign, offset)
      })

    val offsetValue = offset match {
      case Some(value) =>
        hex match {
          case Some(true) =>
            hex2int(value)
          case Some(false) =>
            string2int(value)
          case None =>
            throw new NumberFormatException("Invalid label '" + name + "'")
        }
      case None =>
        0x00
    }

    val change = sign match {
      case Some(true) =>
        offsetValue
      case Some(false) =>
        - offsetValue
      case None =>
        0x00
    }

    try {
      (labelLog(label) + change).toShort
    }
    catch {
      case _: NoSuchElementException =>
        throw new UnsupportedOperationException("Unrecognised label: '" + name + "'")
    }
  }

  protected def labelLog: LabelLog = _labelLog

  protected def labelLog_=(fileName: String): Unit = {
    try {
      _labelLog = LabelLog(fileName)
    }
    catch {
      case e: FileNotFoundException =>
        try {
          _labelLog = LabelLog(fileName.toCharArray.toList)
        }
        catch {
          case _ : Throwable =>
            throw e
        }
    }
  }

  protected def memoryType: MemoryType = _memoryType

  protected def memoryType_=(memoryType: MemoryType): Unit = {
    _memoryType = memoryType
  }

  protected def plus60kEnabled: Boolean = _plus60kEnabled

  protected def plus60kEnabled_=(plus60kEnabled: Boolean): Unit = {
    _plus60kEnabled = plus60kEnabled
  }
}
