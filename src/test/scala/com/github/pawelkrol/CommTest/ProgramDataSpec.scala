package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

import org.scalatest.{ FunSpec => SFunSpec }

import TestUtils._

class ProgramDataSpec extends SFunSpec {

  describe("program data") {
    it("reads program data from file") {
      val programData = ProgramData(testResourceFile("/math.prg"))
      assert(programData.address == 0x1000.toShort)
      assert(programData.program.take(8) == List(0xa5, 0x28, 0x85, 0x23, 0xa9, 0x00, 0x85, 0x22))
    }
  }
}
