package com.github.pawelkrol.CommTest

import org.scalatest.{ FunSpec => SFunSpec }

import TestUtils._

class LabelLogSpec extends SFunSpec {

  describe("label log") {
    it("reads label log from file") {
      val labelLog = LabelLog(testResourceFile("/math.log"))
      assert(labelLog("calculate_deltaerr_dx_div_dy") == 0x1000)
      assert(labelLog("calculate_deltaerr_dy_div_dx") == 0x1018)
      assert(labelLog("divisor") == 0x0020)
      assert(labelLog("deltaerr") == 0x0026)
    }

    it("parses label log from text") {
      val labelLog = LabelLog("""
        init = $1000
      """.toCharArray.toList)
      assert(labelLog("init") == 0x1000)
    }

    it("accepts labels with numeric characters") {
      val labelLog = LabelLog(testResourceFile("/fill.log"))
      assert(labelLog("fill_0100_bytes_with_A") == 0x100c)
    }
  }
}
