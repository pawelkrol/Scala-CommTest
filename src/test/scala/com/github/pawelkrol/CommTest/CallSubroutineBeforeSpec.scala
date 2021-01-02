package com.github.pawelkrol.CommTest

import TestUtils._

class CallSubroutineBeforeSpec extends FunSpec {

  outputPrg = testResourcePath("/mock.prg")

  labelLog = testResourcePath("/mock.log")

  before {
    setCustomHandler("mock4") { XR = 0x00 }
    setCustomHandler("mock4") { YR = 0x01 }
    call("test2")
  }

  describe("subroutine called inside of a 'before' block") {

    it("executes subroutine call successfully") {
      expect(XR).toEqual(1)
      expect(YR).toEqual(1)
    }

    context("with nested subroutine mocks") {

      before {
        setCustomHandler("mock3") { XR = 0x02 }
        call("test2")
      }

      it("executes subroutine call successfully") {
        expect(XR).toEqual(2)
        expect(YR).toEqual(0)
      }
    }
  }
}
