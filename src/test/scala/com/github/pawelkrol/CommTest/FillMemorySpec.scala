package com.github.pawelkrol.CommTest

import TestUtils._

class FillMemorySpec extends FunSpec {

  outputPrg = testResourcePath("/fill.prg")

  labelLog = testResourcePath("/fill.log")

  describe("fill_0100_bytes_with_A") {
    before {
      AC = 0x80
      XR = 0x00
      YR = 0x18
    }

    it("fills memory with AC value") {
      call

      assert(readByteAt(0x1400) === 0xff)
      assert(readByteAt(0x14ff) === 0xff)

      assert(readByteAt(0x1800) === 0x80)
      assert(readByteAt(0x18ff) === 0x80)
    }
  }
}
