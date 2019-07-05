package com.github.pawelkrol.CommTest

import TestUtils._

class CallSubroutineSpec extends FunSpec {

  outputPrg = testResourcePath("/load.prg")

  labelLog = testResourcePath("/load.log")

  describe("decr") {
    before {
      writeByteAt("data", 0x01)
      call // inc data
      call("load") // inc data
    }

    it("calls described and named subroutines inside of a 'before' block") {
      expect(call).toChange(readByteAt("data")).from(0x03).to(0x04)
    }
  }
}
