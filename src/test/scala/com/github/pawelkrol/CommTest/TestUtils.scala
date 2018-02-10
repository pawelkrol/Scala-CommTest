package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.{ ByteVal, Core, OpCode }

import java.io.File

import scala.io.Codec.ISO8859
import scala.io.Source.fromFile

object TestUtils {

  def binaryString(value: ByteVal) = "%8s".format(Integer.toBinaryString(value())).replace(" ", "0")

  def printInstruction(core: Core) {
    val opCode = OpCode(core.memory.read(core.register.PC), core)
    printInstruction(core, opCode)
  }

  def printInstruction(core: Core, opCode: OpCode) {
    val register = core.register

    val bytes = opCode.bytes(core).map(byte => "%02X".format(byte())).mkString(" ")
    val instruction = ".C:%04X %-11s %s %-10s - A:%02X X:%02X Y:%02X SP:%02X %s".format(
      register.PC,
      bytes,
      opCode.symName,
      opCode.argValue(core),
      register.AC(),
      register.XR(),
      register.YR(),
      register.SP(),
      register.statusFlags
    )

    println(instruction)
  }

  def printRegisters(core: Core) {
    val memory = core.memory
    val register = core.register

    val registers = "\n  ADDR AC XR YR SP 00 01 NV-BDIZC\n.;%04x %02x %02x %02x %02x %02x %02x %s".format(
      register.PC,
      register.AC(),
      register.XR(),
      register.YR(),
      register.SP(),
      memory.read(0x0000)(),
      memory.read(0x0001)(),
      binaryString(register.status)
    )

    println(registers)
  }

  def printMemory(core: Core, from: Short, to: Short) {
    val groupSize = 8

    val memory = (from to to).grouped(size = groupSize).toList.map(group => {
      val (values, strings) = group.map(offset => {
        (
          "%02x".format(core.memory.read(offset)()),
          "." // TODO
        )
      }).unzip

      ".:%04x %s%s %s".format(
        group.head,
        values.mkString(" "),
        " " * (groupSize - group.size) * 3,
        strings.mkString
      )
    }).mkString("\n")

    println(memory)
  }

  def testResourcePath(name: String) = getClass.getResource(name).toString.replace("file:", "")

  def testResourceFile(name: String) = new File(testResourcePath(name))

  def testResourceData(file: File) = fromFile(file)(ISO8859).toList

  def testResourceData(name: String) = fromFile(testResourceFile(name))(ISO8859).toList
}
