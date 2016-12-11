package com.github.pawelkrol.CommTest

import TestUtils._

class FunSpecSpec extends FunSpec {

  outputPrg = testResourcePath("/math.prg")

  labelLog = testResourcePath("/math.log")

  sharedExamples("validate execution") {
    it("does not modify input parameters") {
      expect(call).notToChange(readBytesAt("deltax", "deltay"))
    }
  }

  describe("calculate_deltaerr_dx_div_dy") {
    context("dx = $01, dy = $02") {
      before {
        writeByteAt("deltax", 0x01)
        writeByteAt("deltay", 0x02)
      }

      it("computes deltaerr") {
        expect(call).toChange(AC).to(0x80)
      }

      includeExamples("validate execution")
    }
  }

  describe("calculate_deltaerr_dy_div_dx") {
    context("dy = $02, dx = $03") {
      before {
        writeByteAt(0x29, 0x02)
        writeByteAt(0x28, 0x03)
      }

      it("computes deltaerr") {
        expect(call).toChange(AC).to(0xaa)
      }

      includeExamples("validate execution")
    }
  }

  describe("divide") {
    sharedExamples("validate execution") {
      it("repeats divloop for each bit") {
        expect(call).toChange(XR).to(0x00)
      }

      it("finishes computations with a valid fraction") {
        val quotient = get("dividend").asInstanceOf[Int] / get("divisor").asInstanceOf[Int]
        expect(call).toChange(readWordAt("result")).to(quotient.toShort)
      }
    }

    before {
      writeWordAt("dividend", get("dividend").asInstanceOf[Int])
      writeWordAt("divisor", get("divisor").asInstanceOf[Int])
    }

    context("$01 / $02") {
      let("dividend") { 0x0100 }
      let("divisor") { 0x0002 }

      includeExamples("validate execution")
    }

    context("$02 / $03") {
      let("dividend") { 0x0200 }
      let("divisor") { 0x0003 }

      includeExamples("validate execution")
    }
  }
}
